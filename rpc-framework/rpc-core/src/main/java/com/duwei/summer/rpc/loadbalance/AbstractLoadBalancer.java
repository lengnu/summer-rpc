package com.duwei.summer.rpc.loadbalance;

import com.duwei.summer.rpc.context.ApplicationContext;
import com.duwei.summer.rpc.registry.Registry;

import java.net.InetSocketAddress;
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
    protected ApplicationContext applicationContext;

    @Override
    public void setApplication(ApplicationContext application) {
        this.applicationContext = application;
    }

    @Override
    public InetSocketAddress selectServiceAddress(String serviceName) {
        Selector selector = serviceSelectorCache.get(serviceName);
        if (selector == null) {
            synchronized (this) {
                selector = serviceSelectorCache.get(serviceName);
                if (selector == null) {
                    List<InetSocketAddress> serviceAddressList = applicationContext.getRegistry().lookup(serviceName);
                    selector = getSelector(serviceName,serviceAddressList);
                    serviceSelectorCache.put(serviceName, selector);
                }
            }
        }
        return selector.next();
    }

    @Override
    public synchronized void updateServiceList(String serviceName, List<InetSocketAddress> serviceAddressList) {
        serviceSelectorCache.put(serviceName, getSelector(serviceName,serviceAddressList));
    }

    /**
     * 留给子类实现，初始化一个选择器
     *
     * @param serviceAddressList 服务列表
     * @return 对应选择器
     */
    protected abstract Selector getSelector(String serviceName,List<InetSocketAddress> serviceAddressList);
}
