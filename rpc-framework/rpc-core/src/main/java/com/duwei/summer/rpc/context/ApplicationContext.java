package com.duwei.summer.rpc.context;

import com.duwei.summer.rpc.compress.CompressorWrapper;
import com.duwei.summer.rpc.compress.ServiceConfig;
import com.duwei.summer.rpc.loadbalance.LoadBalancer;
import com.duwei.summer.rpc.protection.CircuitBreaker;
import com.duwei.summer.rpc.protection.RateLimiter;
import com.duwei.summer.rpc.registry.NettyBootstrapInitializer;
import com.duwei.summer.rpc.registry.Registry;
import com.duwei.summer.rpc.serialize.SerializerWrapper;
import com.duwei.summer.rpc.transport.ChannelProvider;
import com.duwei.summer.rpc.transport.message.request.RpcRequest;
import com.duwei.summer.rpc.uid.IdWorker;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.CompleteFuture;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 服务端应用上下文
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-24 18:39
 * @since: 1.0
 */
@Slf4j
public class ApplicationContext {
    private Configuration configuration;
    /**
     * 针对IP级别的限流器
     */
    private final Map<InetSocketAddress, RateLimiter> rateLimiterCache = new ConcurrentHashMap<>(16);

    private Registry registry;
    private LoadBalancer loadBalancer;
    private IdWorker idWorker;
    private SerializerWrapper serializerWrapper;
    private CompressorWrapper compressorWrapper;
    /**
     * 配置服务列表
     */
    private final Map<String, ServiceConfig<?>> serviceConfigCache = new ConcurrentHashMap<>(16);
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
    private final Map<Long,CompletableFuture<Object>> waitResponseFuture = new ConcurrentHashMap<>(16);

    public Registry getRegistry() {
        return registry;
    }

    public LoadBalancer getLoadBalancer() {
        return loadBalancer;
    }

    public IdWorker getIdWorker() {
        return idWorker;
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
    public InetSocketAddress selectServiceAddress(String serviceName) {
        return loadBalancer.selectServiceAddress(serviceName);
    }

    public long nextId(){
        return idWorker.nextId();
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress){
        return channelProvider.getChannel(inetSocketAddress);
    }

    public void recordWaitResponseCompleteFuture(Long requestId, CompletableFuture<Object> completeFuture){
        waitResponseFuture.put(requestId,completeFuture);
    }

    public long getTimeout(){
        return configuration.getWaitResponseTimeout();
    }

//    public RateLimiter getRateLimiter(InetSocketAddress inetSocketAddress){
//        rateLimiterMapForIp.putIfAbsent(inetSocketAddress,new TokenBuketRateLimiter())
//    }
}
