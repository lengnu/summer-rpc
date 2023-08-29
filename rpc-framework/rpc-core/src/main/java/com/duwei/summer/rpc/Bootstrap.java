package com.duwei.summer.rpc;

import com.duwei.summer.rpc.annotation.RpcService;
import com.duwei.summer.rpc.close.ShutdownHock;
import com.duwei.summer.rpc.config.ReferenceConfig;
import com.duwei.summer.rpc.config.ServiceConfig;
import com.duwei.summer.rpc.context.ApplicationContext;
import com.duwei.summer.rpc.context.spi.SpiReader;
import com.duwei.summer.rpc.loadbalance.LoadBalancerConfig;
import com.duwei.summer.rpc.protection.limiter.RateLimiter;
import com.duwei.summer.rpc.registry.RegistryConfig;
import com.duwei.summer.rpc.transport.NettyServerStarter;
import com.duwei.summer.rpc.util.ClassScanner;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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
     * 默认配置文件路径
     */
    private static final String DEFAULT_CONFIG_LOCATION = "rpc-config.xml";
    /**
     * 全局配置
     */
    private ApplicationContext applicationContext;
    private volatile NettyServerStarter nettyServerStarter;


    private Bootstrap() {
        applicationContext = new ApplicationContext();
        applicationContext.getRegistryConfig().setApplicationContext(applicationContext);
        applicationContext.getLoadBalancerConfig().setApplicationContext(applicationContext);
        applicationContext.getIdGeneratorConfig().setApplicationContext(applicationContext);
        SpiReader.findService();
        this.load(DEFAULT_CONFIG_LOCATION);
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

    /**
     * 发布服务
     *
     * @param serviceConfig 服务配置
     * @return this
     */
    public Bootstrap publish(ServiceConfig<?> serviceConfig) {
        getApplicationContext().getRegistryConfig().setApplicationContext(applicationContext);
        applicationContext.getRegistryConfig().register(serviceConfig);
        return this;
    }

    public Bootstrap publish(List<ServiceConfig<?>> serviceConfigs){
        serviceConfigs.forEach(this::publish);
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
    public Bootstrap loadBalancerConfig(LoadBalancerConfig loadBalancerConfig) {
        applicationContext.setLoadBalancerConfig(loadBalancerConfig);
        loadBalancerConfig.setApplicationContext(applicationContext);
        return this;
    }

    /**
     * 设置需要发现的服务
     *
     * @param referenceConfig 需要发现的服务配置
     * @return this
     */
    public Bootstrap reference(ReferenceConfig<?> referenceConfig) {
        referenceConfig.setApplicationContext(applicationContext);
        return this;
    }

    public Bootstrap rateLimiter(RateLimiter rateLimiter) {
        this.applicationContext.setRateLimiter(rateLimiter);
        return this;
    }

    public Bootstrap earlyConnect(boolean allow) {
        this.applicationContext.setEarlyConnect(allow);
        return this;
    }

    public Bootstrap scanService(String basePackage) {
        ClassScanner.scan(basePackage, RpcService.class).forEach(tClass -> {
            try {
                ServiceConfig.build(tClass).forEach(this::publish);
            } catch (Exception e) {
                log.error("服务{}发布失败", tClass);
            }
        });
        return this;
    }


    /**
     * 启动netty服务端
     */
    public void start() {
        if (nettyServerStarter == null) {
            Runtime.getRuntime().addShutdownHook(new ShutdownHock(applicationContext));
            synchronized (this) {
                if (nettyServerStarter == null) {
                    this.nettyServerStarter = new NettyServerStarter(applicationContext);
                    nettyServerStarter.start();
                }
            }
        }

    }

}
