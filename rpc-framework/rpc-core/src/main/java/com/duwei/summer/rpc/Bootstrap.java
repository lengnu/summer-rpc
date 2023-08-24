package com.duwei.summer.rpc;

import com.duwei.summer.rpc.compress.Compressor;
import com.duwei.summer.rpc.compress.CompressorWrapper;
import com.duwei.summer.rpc.compress.ServiceConfig;
import com.duwei.summer.rpc.context.Configuration;
import com.duwei.summer.rpc.discovery.ReferenceConfig;
import com.duwei.summer.rpc.loadbalance.LoadBalancer;
import com.duwei.summer.rpc.registry.NettyBootstrapInitializer;
import com.duwei.summer.rpc.registry.RegistryConfig;
import com.duwei.summer.rpc.serialize.Serializer;
import com.duwei.summer.rpc.serialize.SerializerWrapper;
import com.duwei.summer.rpc.transport.ChannelProvider;
import com.duwei.summer.rpc.uid.IdWorker;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 启动类
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-21 21:45
 * @since: 1.0
 */
@Slf4j
@Data
public class Bootstrap {
    private static final Bootstrap bootstrap = new Bootstrap();
    /**
     * 全局配置
     */
    private Configuration configuration;
    private ChannelProvider channelProvider;
    private NettyBootstrapInitializer nettyBootstrapInitializer;
    /**
     * ID生成器
     */
    private IdWorker idWorker;

    public IdWorker getIdWorker() {
        return idWorker;
    }

    /**
     * 序列化器
     */
    private SerializerWrapper serializerWrapper;

    public SerializerWrapper getSerializerWrapper() {
        return serializerWrapper;
    }

    /**
     * 压缩器
     */
    private CompressorWrapper compressorWrapper;

    public CompressorWrapper getCompressorWrapper() {
        return compressorWrapper;
    }

    public static ServiceConfig<?> getServiceConfig(String serviceName) {
        return serviceList.get(serviceName);
    }

    /**
     * 已经发布且暴露的服务列表
     * key： interface全限定名
     * value：服务配置
     */
    private static Map<String, ServiceConfig<?>> serviceList = new ConcurrentHashMap<>(16);

    /**
     * 连接缓存
     */
    private static Map<InetSocketAddress, Channel> cacheChannels = new ConcurrentHashMap<>(64);

    /**
     * 挂起的请求
     */
    private static Map<Long, CompletableFuture<?>> pendingRequest = new ConcurrentHashMap<>(16);

    private Bootstrap() {

    }


    public static Bootstrap getInstance() {
        return bootstrap;
    }


    /**
     * 配置应用程序端口
     *
     * @param port 端口号
     * @return this
     */
    public Bootstrap port(int port) {
        this.configuration.setPort(port);
        return this;
    }

    /**
     * 定义应用的名字
     *
     * @param applicationName 应用名字
     * @return this
     */
    public Bootstrap application(String applicationName) {
        this.configuration.setApplication(applicationName);
        return this;
    }


    /**
     * 注册中心配置
     *
     * @param registryConfig 注册中心配置
     * @return this
     */
    public Bootstrap registry(RegistryConfig registryConfig) {
        configuration.setRegistryConfig(registryConfig);
        return this;
    }

    /**
     * 配置负载均衡策略
     *
     * @param loadBalancer 负载均衡配置
     * @return this
     */
    public Bootstrap loadBalancer(LoadBalancer loadBalancer) {
        configuration.setLoadBalancer(loadBalancer);
        return this;
    }

    /**
     * 发布服务
     *
     * @param serviceConfig 服务相关配置
     * @return this
     */
    public Bootstrap publish(ServiceConfig<?> serviceConfig) {
        // 服务名称节点，持久节点
        this.configuration.getRegistryConfig().getRegistry().registry(serviceConfig);
        serviceList.put(serviceConfig.getInterfaceProvider().getName(), serviceConfig);
        return this;
    }

    /**
     * 服务批量发布
     *
     * @param serviceConfigs 服务列表
     * @return this
     */
    public Bootstrap publish(List<ServiceConfig<?>> serviceConfigs) {
        serviceConfigs.forEach(this::publish);
        return this;
    }

    /**
     * 配置序列化方式
     *
     * @param serializerType 序列化类型
     * @return this
     */
    public Bootstrap serializer(String serializerType) {
        if (log.isDebugEnabled()) {
            log.debug("序列化方式为{}", serializerType);
        }
        return this;
    }

    /**
     * 压缩协议配置
     *
     * @param compressType 压缩协议配置
     * @return this
     */
    public Bootstrap compressor(String compressType) {
        configuration.setCompressName(compressType);
        return this;
    }


    /**
     * 启动服务
     */
    public void start() {

    }


    public Bootstrap reference(ReferenceConfig<?> referenceConfig) {
//        referenceConfig.setRegistry(registryConfig.getRegistry());
//        referenceConfig.setBootstrap(this);
        return this;
    }

    /**
     * 根据地址获取一个可用通道
     */
    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        return cacheChannels.get(inetSocketAddress);
    }

    public void addChannel(InetSocketAddress inetSocketAddress, Channel channel) {
        cacheChannels.put(inetSocketAddress, channel);
    }

    /**
     * 拿到包下的所有类全限定名
     *
     * @param basePackage 基础包
     * @return this
     */
    public Bootstrap scan(String basePackage) {
        return this;
    }
}
