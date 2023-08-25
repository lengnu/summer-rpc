package com.duwei.summer.rpc;

import com.duwei.summer.rpc.config.ReferenceConfig;
import com.duwei.summer.rpc.config.ServiceConfig;
import com.duwei.summer.rpc.context.ApplicationContext;
import com.duwei.summer.rpc.loadbalance.LoadBalancerConfig;
import com.duwei.summer.rpc.transport.NettyBootstrapInitializer;
import com.duwei.summer.rpc.registry.RegistryConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.omg.PortableServer.THREAD_POLICY_ID;

import java.io.IOException;
import java.util.Set;

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
    private ApplicationContext applicationContext;
    private NettyBootstrapInitializer nettyBootstrapInitializer;


    private Bootstrap() {
        applicationContext = new ApplicationContext();
    }

    public Bootstrap load(String resource) {
        applicationContext.refresh(resource);
        return this;
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
        this.applicationContext.setPort(port);
        return this;
    }

    /**
     * 定义应用的名字
     *
     * @param applicationName 应用名字
     * @return this
     */
    public Bootstrap applicationName(String applicationName) {
        this.applicationContext.setApplicationName(applicationName);
        return this;
    }

    public Bootstrap publish(ServiceConfig<?> serviceConfig) {
        applicationContext.getRegistryConfig().register(serviceConfig);
        return this;
    }


    /**
     * 注册中心配置
     *
     * @param registryConfig 注册中心配置
     * @return this
     */
    public Bootstrap registryConfig(RegistryConfig registryConfig) {
        applicationContext.setRegistryConfig(registryConfig);
        registryConfig.setApplicationContext(applicationContext);
        return this;
    }

    /**
     * 负载均衡配置
     */
    public Bootstrap loadBalancerConfig(LoadBalancerConfig loadBalancerConfig){
        applicationContext.setLoadBalancerConfig(loadBalancerConfig);
        loadBalancerConfig.setApplicationContext(applicationContext);
        return this;
    }

    public Bootstrap reference(ReferenceConfig<?> referenceConfig){
        referenceConfig.setApplicationContext(applicationContext);
        return this;
    }


}
