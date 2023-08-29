package com.duwei.summer.rpc.loadbalance;

import com.duwei.summer.rpc.context.ApplicationContext;
import com.duwei.summer.rpc.registry.Registry;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 提取公共的方法
 * 1. 初始化Selector
 * 2. 缓存Selector
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-23 15:40
 * @since: 1.0
 */
public abstract class AbstractLoadBalancer implements LoadBalancer {
    private final Map<String, Selector> serviceSelectorCache = new ConcurrentHashMap<>(8);
    private LoadBalancerConfig loadBalancerConfig;

    @Override
    public void init(LoadBalancerConfig loadBalancerConfig) {
        this.loadBalancerConfig = loadBalancerConfig;
        this.init();
    }

    protected abstract void init();


    public LoadBalancerConfig getLoadBalancerConfig() {
        return loadBalancerConfig;
    }

    @Override
    public List<InetSocketAddress> getServiceAddress(String serviceName) {
        Selector selector = serviceSelectorCache.get(serviceName);
        if (selector == null){
            return Collections.emptyList();
        }
        return selector.getAll();
    }

    @Override
    public InetSocketAddress selectServiceAddress(String serviceName,String group) {
        Selector selector = serviceSelectorCache.get(serviceName);
        if (selector == null) {
            synchronized (this) {
                selector = serviceSelectorCache.get(serviceName);
                ApplicationContext applicationContext = getLoadBalancerConfig().getApplicationContext();
                if (selector == null) {
                    List<InetSocketAddress> serviceAddressList =
                            applicationContext.getRegistryConfig().getRegistry().lookup(serviceName, group);
                    // 如果允许，则直接在发现服务时候进行连接建立
                    if(applicationContext.isEarlyConnect()){
                        serviceAddressList.forEach((serviceAddress) -> applicationContext.getChannelProvider().getChannel(serviceAddress));
                    }
                    selector = getSelector(serviceName, serviceAddressList);
                    serviceSelectorCache.put(serviceName, selector);
                }
            }
        }
        return selector.next();
    }

    @Override
    public synchronized void updateServiceList(String serviceName, List<InetSocketAddress> serviceAddressList) {
        ApplicationContext applicationContext = getLoadBalancerConfig().getApplicationContext();
        if (applicationContext.isEarlyConnect()){
            serviceAddressList.forEach((serviceAddress) -> applicationContext.getChannelProvider().getChannel(serviceAddress));
        }
        serviceSelectorCache.put(serviceName, getSelector(serviceName, serviceAddressList));
    }

    /**
     * 留给子类实现，初始化一个选择器
     * @param serviceName 服务名称
     * @param serviceAddressList 服务列表
     * @return 对应选择器
     */
    protected abstract Selector getSelector(String serviceName, List<InetSocketAddress> serviceAddressList);
}
