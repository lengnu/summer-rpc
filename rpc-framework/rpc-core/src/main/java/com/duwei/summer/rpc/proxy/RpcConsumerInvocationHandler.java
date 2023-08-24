package com.duwei.summer.rpc.proxy;

import com.duwei.summer.rpc.Bootstrap;
import com.duwei.summer.rpc.annotation.Retry;
import com.duwei.summer.rpc.context.ApplicationContext;
import com.duwei.summer.rpc.protection.CircuitBreaker;
import com.duwei.summer.rpc.retry.FixedIntervalRetryPolicy;
import com.duwei.summer.rpc.retry.RetryPolicy;
import com.duwei.summer.rpc.retry.RetryPolicyMetadataHolder;
import com.duwei.summer.rpc.transport.message.request.RequestPayload;
import com.duwei.summer.rpc.transport.message.request.RequestType;
import com.duwei.summer.rpc.transport.message.request.RpcRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.CompleteFuture;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * RPC客户端的代理Invocation
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 19:26
 * @since: 1.0
 */
@Slf4j
@Data
public class RpcConsumerInvocationHandler implements InvocationHandler {
    private Class<?> interfaceName;
    private ApplicationContext applicationContext;


    private Map<Method, RetryPolicyMetadataHolder> retryPolicyMetadataHolderMap = new HashMap<>(16);

    public RpcConsumerInvocationHandler(Class<?> interfaceName, ApplicationContext applicationContext) {
        this.interfaceName = interfaceName;
        this.applicationContext = applicationContext;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        while (true) {
            // 1.构建请求
            RpcRequest rpcRequest = buildRequest(method, args);
            // 2.将请求放入当前上下文
            applicationContext.setRpcRequest(rpcRequest);
            // 3. 根据负载均衡策略拉取一个服务
            InetSocketAddress serviceAddress = applicationContext.selectServiceAddress(interfaceName.getName());
            if (log.isDebugEnabled()) {
                log.debug("接口{}即将请求{}提供的服务", interfaceName.getName(), serviceAddress);
            }
            // 4.获取该服务对应的熔断器
            CircuitBreaker circuitBreaker = applicationContext.getCircuitBreaker(serviceAddress);
            try {
                // 非心跳检测，且熔断器打开了
                if (rpcRequest.getRequestType() != RequestType.HEART_BEAT.getId() && circuitBreaker.isBreak()) {
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {

                        }
                    }, 5000L);
                }

                // 获取可用信道
                Channel channel = applicationContext.getChannel(serviceAddress);
                CompletableFuture<Object> resultFuture = new CompletableFuture<>();
                applicationContext.recordWaitResponseCompleteFuture(rpcRequest.getRequestId(), resultFuture);

                // 发送请求
                channel.writeAndFlush(rpcRequest).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        resultFuture.completeExceptionally(channelFuture.cause());
                    }
                });

                // 清理当前上下文的请求对象
                applicationContext.removeRpcRequest();

                // 获取结果
                Object result = resultFuture.get(applicationContext.getTimeout(), TimeUnit.MILLISECONDS);

                // 记录熔断器
                // TODO
                return result;

            } catch (Exception e) {

            }

        }

    }


    /**
     * 构建请求
     */
    public RpcRequest buildRequest(Method method, Object[] args) {
        return RpcRequest.builder()
                .requestId(applicationContext.getIdWorker().nextId())
                .serializeType(applicationContext.getSerializerWrapper().getType())
                .compressedType(applicationContext.getCompressorWrapper().getType())
                .timeStamp(System.currentTimeMillis())
                .requestType(RequestType.COMMON_REQUEST.getId())
                .requestPayload(buildPayload(method, args))
                .build();
    }

    /**
     * 构建请求负载
     *
     * @param method 方法句柄
     * @param args   方法参数
     * @return 请求负载
     */
    private RequestPayload buildPayload(Method method, Object[] args) {
        return RequestPayload.builder()
                .interfaceName(interfaceName.getName())
                .methodName(method.getName())
                .parametersType(method.getParameterTypes())
                .parametersValue(args)
                .returnType(method.getReturnType())
                .build();
    }

    private RetryPolicy getRetryPolicy(Method method) {
        if (!retryPolicyMetadataHolderMap.containsKey(method)) {
            Retry retry = method.getAnnotation(Retry.class);
            if (retry != null) {
                retryPolicyMetadataHolderMap.put(method, new RetryPolicyMetadataHolder(retry));
            } else {
                retryPolicyMetadataHolderMap.put(method, new RetryPolicyMetadataHolder(0, 0, FixedIntervalRetryPolicy.class))
            }
        }
        return retryPolicyMetadataHolderMap.get(method).getRetryPolicy();
    }


}
