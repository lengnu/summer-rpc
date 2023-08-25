package com.duwei.summer.rpc.context;

import com.duwei.summer.rpc.compress.CompressorFactory;
import com.duwei.summer.rpc.compress.CompressorWrapper;
import com.duwei.summer.rpc.context.xml.XmlReader;
import com.duwei.summer.rpc.loadbalance.LoadBalancerConfig;
import com.duwei.summer.rpc.loadbalance.LoadBalancerConfigs;
import com.duwei.summer.rpc.protection.CircuitBreaker;
import com.duwei.summer.rpc.protection.RateLimiter;
import com.duwei.summer.rpc.registry.RegistryConfig;
import com.duwei.summer.rpc.registry.RegistryConfigs;
import com.duwei.summer.rpc.serialize.SerializerFactory;
import com.duwei.summer.rpc.serialize.SerializerWrapper;
import com.duwei.summer.rpc.transport.ChannelProvider;
import com.duwei.summer.rpc.transport.RequestHolder;
import com.duwei.summer.rpc.transport.message.request.RpcRequest;
import com.duwei.summer.rpc.uid.IdGenerator;
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
@Data
@NoArgsConstructor
public class ApplicationContext {
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
    private long waitResponseTimeout = 2000;
    /**
     * 针对IP级别的限流器
     */
    private final Map<InetSocketAddress, RateLimiter> rateLimiterCache = new ConcurrentHashMap<>(16);

    private RegistryConfig registryConfig = RegistryConfigs.newZookeeperRegistryConfig("127.0.0.1",2181);
    private LoadBalancerConfig loadBalancerConfig = LoadBalancerConfigs.newConsistentHashLoadBalancerConfig(20);
    private IdGeneratorConfig idGeneratorConfig = IdGeneratorConfigs.newSnowflakeIdGeneratorConfig(1,1);
    private SerializerWrapper serializerWrapper = SerializerFactory.getSerializerWrapper("hessian");
    private CompressorWrapper compressorWrapper = CompressorFactory.getCompressorWrapper("gzip");

    /**
     * 维护当前每个线程正在处理的请求
     */
    private final RequestHolder requestHolder = new RequestHolder();
    /**
     * 维护当前上下文中所有已经建立的连接
     */
    private final ChannelProvider channelProvider = new ChannelProvider();

    /**
     * 记录每个地址的熔断器
     */
    private final Map<InetSocketAddress, CircuitBreaker> circuitBreakerCache = new ConcurrentHashMap<>(16);
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
    public CircuitBreaker getCircuitBreaker(InetSocketAddress inetSocketAddress) {
        circuitBreakerCache.putIfAbsent(inetSocketAddress, new CircuitBreaker());
        return circuitBreakerCache.get(inetSocketAddress);
    }

    /**
     * 重置熔断器
     */
    public void resetCircuitBreaker(InetSocketAddress inetSocketAddress) {
        getCircuitBreaker(inetSocketAddress).reset();
    }

    /**
     * 记录Rpc请求到当前的上下文中
     */
    public void setRpcRequest(RpcRequest rpcRequest) {
        requestHolder.setRpcRequest(rpcRequest);
    }

    /**
     * 获取当前上下文的请求
     */
    public RpcRequest getRpcRequest() {
        return requestHolder.getRpcRequest();
    }

    /**
     * 删除线程本地上下文中的请求
     */
    public void removeRpcRequest() {
        requestHolder.removeRpcRequest();
    }

    /**
     * 从上下文中选择一个服务地址
     */
    public InetSocketAddress selectServiceAddress(String serviceName,String group) {
        return loadBalancerConfig.getLoadBalancer().selectServiceAddress(serviceName,group);
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        return channelProvider.getChannel(inetSocketAddress);
    }


    public long nextId(){
        return idGeneratorConfig.nextId();
    }


    public void recordWaitResponseCompleteFuture(Long requestId, CompletableFuture<Object> completeFuture) {
        waitResponseFuture.put(requestId, completeFuture);
    }


    public long getWaitResponseTimeout() {
        return waitResponseTimeout;
    }

    /**
     * 重新加载配置文件的内容
     * @param resource  配置文件路径
     */
    public synchronized void refresh(String resource){
        XmlReader xmlReader = new XmlReader(this);
        xmlReader.load(resource);
    }

}
