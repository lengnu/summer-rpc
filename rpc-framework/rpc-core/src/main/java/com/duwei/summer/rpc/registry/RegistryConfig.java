package com.duwei.summer.rpc.registry;

import com.duwei.summer.rpc.config.BaseConfig;
import com.duwei.summer.rpc.config.ServiceConfig;
import com.duwei.summer.rpc.context.ApplicationContext;
import com.duwei.summer.rpc.exception.RegistryException;
import com.duwei.summer.rpc.property.AttributeAccessorSupport;
import com.duwei.summer.rpc.registry.zk.ZookeeperRegistry;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 注册中心配置，根据配置的注册中心连接地址初始化注册中心
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-21 21:49
 * @since: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class RegistryConfig extends BaseConfig {
    private String host;
    private int port;
    private Class<? extends Registry> registryClass;
    private volatile Registry registry;
    private Map<String, ServiceConfig<?>> serviceProviderCache = new ConcurrentHashMap<>(16);

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setRegistryClass(Class<? extends Registry> registryClass) {
        this.registryClass = registryClass;
    }

    public void register(ServiceConfig<?> serviceConfig) {
        try {
            getRegistry().registry(serviceConfig);
        } catch (Exception e) {
            log.error("服务注册异常");
            return;
        }
        serviceProviderCache.put(serviceConfig.getInterfaceProvider().getName(), serviceConfig);
    }

    public RegistryConfig() {

    }

    public List<InetSocketAddress> lookup(String serviceName,String group) {
        return getRegistry().lookup(serviceName, group);
    }

    /**
     * 懒加载的注册中心
     */
    public Registry getRegistry() {
        if (registry == null) {
            synchronized (this) {
                if (registry == null) {
                    try {
                        registry = registryClass.getConstructor().newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        log.error("注册中心初始化异常，必须提供可访问的无参构造器");
                        throw new RegistryException("注册中心初始化异常，必须提供可访问的无参构造器");
                    }
                    registry.init(this);
                }
            }
        }
        return registry;
    }

    @Override
    public String toString() {
        return "RegistryConfig{" +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", registryClass=" + registryClass +
                '}';
    }
}
