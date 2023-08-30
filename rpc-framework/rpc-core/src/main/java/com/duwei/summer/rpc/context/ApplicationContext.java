package com.duwei.summer.rpc.context;

import com.duwei.summer.rpc.compress.CompressorFactory;
import com.duwei.summer.rpc.compress.CompressorWrapper;
import com.duwei.summer.rpc.config.ServiceConfig;
import com.duwei.summer.rpc.context.xml.XmlReader;
import com.duwei.summer.rpc.loadbalance.LoadBalancerConfig;
import com.duwei.summer.rpc.loadbalance.LoadBalancerConfigs;
import com.duwei.summer.rpc.protection.breaker.CircuitBreaker;
import com.duwei.summer.rpc.protection.limiter.RateLimiter;
import com.duwei.summer.rpc.protection.limiter.RateLimiters;
import com.duwei.summer.rpc.registry.RegistryConfig;
import com.duwei.summer.rpc.registry.RegistryConfigs;
import com.duwei.summer.rpc.serialize.SerializerFactory;
import com.duwei.summer.rpc.serialize.SerializerWrapper;
import com.duwei.summer.rpc.transport.ChannelProvider;
import com.duwei.summer.rpc.transport.message.request.RpcRequest;
import com.duwei.summer.rpc.uid.IdGeneratorConfig;
import com.duwei.summer.rpc.uid.IdGeneratorConfigs;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 * 应用上下文
 * 1. 维护已经建立的连接
 * 2. 针对IP级别的限流器
 * 3  当前线程的请求
 * 4. 所有等待响应的请求
 * 5. 注册中心
 * 6. 所有注册过的服务
 * 7. 序列化器
 * 8. 压缩编码器
 * 9. ID生成器
 * 10. 负载均衡器
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-24 18:39
 * @since: 1.0
 */
@Slf4j
@NoArgsConstructor
@Data
public class ApplicationContext {
    private static final long CLOSING_TIME_OUT = 10 * 1000L;
    private static final long CLOSING_TIME_UNIT = 500L;
    /**
     * 与服务器端的会话超时时长
     */
    private static final long SESSION_TIME_OUT = 60L;

    /**
     * 记录当前上下文的激活状态
     */
    private AtomicBoolean active = new AtomicBoolean(true);
    /**
     * 默认等待响应的超时时间
     */
    private static final long DEFAULT_RESPONSE_TIMEOUT = 5L;
    /**
     * 端口
     */
    private int port = 8888;
    /**
     * 应用名称
     */
    private String applicationName = "default";
    /**
     * 超过市场认为响应失败
     */
    private long waitResponseTimeout = DEFAULT_RESPONSE_TIMEOUT;
    /**
     * 是否建立早期连接，即发现服务后就直接与进行连接进行心跳检测
     */
    private boolean earlyConnect = true;

    private RegistryConfig registryConfig = RegistryConfigs.newZookeeperRegistryConfig("127.0.0.1", 2181);
    private LoadBalancerConfig loadBalancerConfig = LoadBalancerConfigs.newConsistentHashLoadBalancerConfig(20);
    private IdGeneratorConfig idGeneratorConfig = IdGeneratorConfigs.newSnowflakeIdGeneratorConfig(1, 1);
    private SerializerWrapper serializerWrapper = SerializerFactory.getSerializerWrapper("hessian");
    private CompressorWrapper compressorWrapper = CompressorFactory.getCompressorWrapper("gzip");
    /**
     * 限流器，采用默认令牌桶算法
     */
    private RateLimiter rateLimiter = RateLimiters.newTokenBucketRateLimiter();

    /**
     * 维护当前每个线程正在处理的请求
     */
    private final ThreadRequestHolder threadRequestHolder = ThreadRequestHolder.getInstance();
    /**
     * 记录服务端正在处理以及累计的请求
     */
    private final RequestCounter requestCounter = RequestCounter.getInstance();
    /**
     * 维护当前上下文中所有已经建立的连接
     */
    private final ChannelProvider channelProvider = new ChannelProvider(this);

    /**
     * 记录每个服务
     */
    private final Map<String, CircuitBreaker> circuitBreakerCache = new ConcurrentHashMap<>(16);
    /**
     * 记录已经发出但是未回来的请求
     */
    private final Map<Long, CompletableFuture<Object>> waitResponseFuture = new ConcurrentHashMap<>(16);

    public RegistryConfig getRegistryConfig() {
        return registryConfig;
    }

    public SerializerWrapper getSerializerWrapper() {
        return serializerWrapper;
    }

    public CompressorWrapper getCompressorWrapper() {
        return compressorWrapper;
    }

    /**
     * 获取地址对应的熔断器
     */
    public CircuitBreaker getCircuitBreaker(String serviceName) {
        circuitBreakerCache.putIfAbsent(serviceName, new CircuitBreaker());
        return circuitBreakerCache.get(serviceName);
    }

    /**
     * 记录Rpc请求到当前的上下文中
     */
    public void setRpcRequest(RpcRequest rpcRequest) {
        threadRequestHolder.setRpcRequest(rpcRequest);
    }

    /**
     * 获取当前上下文的请求
     */
    public RpcRequest getRpcRequest() {
        return threadRequestHolder.getRpcRequest();
    }

    /**
     * 删除线程本地上下文中的请求
     */
    public void removeRpcRequest() {
        threadRequestHolder.removeRpcRequest();
    }

    /**
     * 从上下文中选择一个服务地址
     */
    public InetSocketAddress selectServiceAddress(String serviceName, String group) {
        return loadBalancerConfig.getLoadBalancer().selectServiceAddress(serviceName, group);
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress ) {
        return channelProvider.getChannel(inetSocketAddress);
    }


    public long nextId() {
        return idGeneratorConfig.nextId();
    }


    public void recordWaitResponseCompleteFuture(Long requestId, CompletableFuture<Object> completeFuture) {
        waitResponseFuture.put(requestId, completeFuture);
    }

    public boolean allowRequest() {
        return rateLimiter.allow();
    }


    public long getWaitResponseTimeout() {
        return waitResponseTimeout;
    }

    /**
     * 当前是否处于活跃状态
     */
    public boolean isActive() {
        return active.get();
    }

    public void countRequest() {
        requestCounter.count();
    }

    public void finishRequest() {
        requestCounter.finish();
    }

    public ServiceConfig<?> getService(String serviceName) {
        return getRegistryConfig().getServiceProviderCache().get(serviceName);
    }

    /**
     * 关闭当前应用程序上下文
     */
    public void close() {
        if (isActive()) {
            // 1.设置上下文为未激活状态
            active.compareAndSet(true, false);

            // 2.等待所有服务处理完毕
            long wait = CLOSING_TIME_OUT;
            while (getProcessingRequest() > 0 && wait > 0) {
                try {
                    Thread.sleep(CLOSING_TIME_UNIT);
                    wait -= CLOSING_TIME_UNIT;
                } catch (InterruptedException ignored) {

                }
            }
            log.info("服务端关闭成功");
        }
    }

    /**
     * 清除等待获取响应的completableFuture
     *
     * @param requestId 请求ID
     */
    public void clearWaitResponseFuture(Long requestId) {
        waitResponseFuture.remove(requestId);
    }

    /**
     * 将服务器返回的响应结果进行填充
     *
     * @param id       请求ID
     * @param response 响应结果
     */
    public void completeResponse(Long id, Object response) {
        CompletableFuture<Object> future = waitResponseFuture.get(id);
        if (future != null) {
            future.complete(response);
        }
        this.clearWaitResponseFuture(id);
    }

    /**
     * 将异常进行填充
     *
     * @param id       请求ID
     * @param e        异常
     */
    public void completeException(Long id, Exception e) {
        CompletableFuture<Object> future = waitResponseFuture.get(id);
        if (future != null) {
            future.completeExceptionally(e);
        }
        this.clearWaitResponseFuture(id);
    }

    /**
     * 获取服务端所有已经处理过的请求
     *
     * @return 处理过的请求数量
     */
    public int getProcessedRequest() {
        return requestCounter.getProcessingRequest();
    }

    /**
     * 获取服务端所有正在处理过的请求
     *
     * @return 正在处理的请求数量
     */
    public int getProcessingRequest() {
        return requestCounter.getProcessingRequest();
    }


    public long getSessionTimeOut() {
        return SESSION_TIME_OUT;
    }

    /**
     * 重新加载配置文件的内容
     *
     * @param resource 配置文件路径
     */
    public synchronized void refresh(String resource) {
        XmlReader xmlReader = new XmlReader(this);
        xmlReader.load(resource);
    }

    /**
     * 是否进行早期连接，即发现服务后就直接建立连接
     */
    public boolean isEarlyConnect() {
        return earlyConnect;
    }

    public void setEarlyConnect(boolean earlyConnect) {
        this.earlyConnect = earlyConnect;
    }
}
