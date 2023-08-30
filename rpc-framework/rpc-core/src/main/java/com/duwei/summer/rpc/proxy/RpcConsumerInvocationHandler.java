package com.duwei.summer.rpc.proxy;

import com.duwei.summer.rpc.annotation.Retry;
import com.duwei.summer.rpc.context.ApplicationContext;
import com.duwei.summer.rpc.exception.CircuitBreakException;
import com.duwei.summer.rpc.exception.RpcRemoteInvokeException;
import com.duwei.summer.rpc.protection.breaker.CircuitBreaker;
import com.duwei.summer.rpc.retry.FixedIntervalRetryPolicy;
import com.duwei.summer.rpc.retry.RetryPolicy;
import com.duwei.summer.rpc.retry.RetryPolicyMetadataHolder;
import com.duwei.summer.rpc.transport.message.request.RequestPayload;
import com.duwei.summer.rpc.transport.message.request.RequestType;
import com.duwei.summer.rpc.transport.message.request.RpcRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
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
    private String group;
    private ApplicationContext applicationContext;
    private long waitResponseTimeout;
    private Map<Method, RetryPolicyMetadataHolder> retryPolicyMetadataHolderMap = new HashMap<>(16);

    public RpcConsumerInvocationHandler(Class<?> interfaceName, String group, ApplicationContext applicationContext) {
        this.interfaceName = interfaceName;
        this.group = group;
        this.applicationContext = applicationContext;
        this.waitResponseTimeout = applicationContext.getWaitResponseTimeout();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // 获取超时策略
        RetryPolicy retryPolicy = getRetryPolicy(method);

        while (true) {
            // 1.构建请求
            RpcRequest rpcRequest = buildRequest(method, args);
            // 2.将请求放入当前上下文
            applicationContext.setRpcRequest(rpcRequest);
            // 3. 根据负载均衡策略拉取一个服务
            InetSocketAddress serviceAddress = applicationContext.selectServiceAddress(interfaceName.getName(), group);
            if (log.isDebugEnabled()) {
                log.debug("接口{}即将请求{}提供的服务", interfaceName.getName(), serviceAddress);
            }
            // 4.获取该服务对应的熔断器
            String serviceName = interfaceName.getName();
            CircuitBreaker circuitBreaker = applicationContext.getCircuitBreaker(serviceName);
            try {
                // 不是心跳请求且熔断器打开，那么无法发送
                if (rpcRequest.getRequestType() != RequestType.HEART_BEAT.getId() && !circuitBreaker.allowRequest()) {
                    throw new CircuitBreakException("服务被熔断，无法进行调用");
                }
                // 获取可用信道
                Channel channel = applicationContext.getChannel(serviceAddress);
                CompletableFuture<Object> resultFuture = new CompletableFuture<>();
                applicationContext.recordWaitResponseCompleteFuture(rpcRequest.getRequestId(), resultFuture);

                // 发送请求
                log.info("请求构建成功，开始向远程服务器发送请求，远程服务器地址{}", channel.remoteAddress());
                channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) channelFuture -> {
                    if (!channelFuture.isSuccess()) {
                        log.error("客户端消息发送失败，请检查连接{}", channel.remoteAddress());
                        resultFuture.completeExceptionally(channelFuture.cause());
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("消息发送成功，等待服务器响应");
                        }
                    }
                });
                Object res = resultFuture.get(waitResponseTimeout, TimeUnit.SECONDS);
                applicationContext.getCircuitBreaker(serviceName).recordSuccess();
                return res;

            } catch (Exception e) {
                applicationContext.getCircuitBreaker(serviceName).recordFailure();
                if (retryPolicy.hasRetryTimes()) {
                    try {
                        Thread.sleep(retryPolicy.nextIntervalTime());
                    } catch (InterruptedException ex) {
                        log.error("重试等待过程中被打断,方法执行失败");
                        throw new RpcRemoteInvokeException("重试等待过程中被打断,方法执行失败");
                    }
                } else {
                    log.error("重试次数用完，方法执行失败");
                    throw new RpcRemoteInvokeException("方法执行失败");
                }
            } finally {
                // 清理当前上下文的请求对象
                applicationContext.removeRpcRequest();
            }
        }
    }

    /**
     * 构建请求
     */
    public RpcRequest buildRequest(Method method, Object[] args) {
        return RpcRequest.builder()
                .requestId(applicationContext.nextId())
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
                retryPolicyMetadataHolderMap.put(method, new RetryPolicyMetadataHolder(RetryPolicy.DEFAULT_RETRY_TIMES, RetryPolicy.DEFAULT_RETRY_INTERVAL, FixedIntervalRetryPolicy.class));
            }
        }
        return retryPolicyMetadataHolderMap.get(method).getRetryPolicy();
    }


}
