package com.duwei.summer.rpc.loadbalance;

import com.duwei.summer.rpc.config.BaseConfig;
import com.duwei.summer.rpc.exception.RegistryException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;

/**
 * <p>
 * 负载均衡配置类
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-25 19:13
 * @since: 1.0
 */
@Slf4j
public class LoadBalancerConfig extends BaseConfig {
    private volatile LoadBalancer loadBalancer;
    private Class<? extends LoadBalancer> loadbalancerClass;

    public void setLoadbalancerClass(Class<? extends LoadBalancer> loadbalancerClass) {
        this.loadbalancerClass = loadbalancerClass;
    }

    public LoadBalancer getLoadBalancer() {
        if (loadBalancer == null) {
            synchronized (this) {
                if (loadBalancer == null) {
                    try {
                        loadBalancer = loadbalancerClass.getConstructor().newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        log.error("负载均衡器初始化异常，必须提供可访问的无参构造器");
                        throw new RegistryException("负载均衡器初始化异常，必须提供可访问的无参构造器");
                    }
                    loadBalancer.init(this);
                }
            }
        }
        return loadBalancer;
    }
}
