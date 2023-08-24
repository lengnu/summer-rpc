package com.duwei.summer.rpc.loadbalance.impl;

import com.duwei.summer.rpc.exception.LoadBalanceException;
import com.duwei.summer.rpc.loadbalance.AbstractLoadBalancer;
import com.duwei.summer.rpc.loadbalance.LoadBalancer;
import com.duwei.summer.rpc.loadbalance.Selector;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 基于轮询策略的负载均衡器
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-23 15:26
 * @since: 1.0
 */
@Slf4j
public class RoundRobinLoadBalancer extends AbstractLoadBalancer {
    @Override
    protected Selector getSelector(String serviceName, List<InetSocketAddress> serviceAddressList) {
        return new RoundRobinSelector(serviceAddressList);
    }

    private static class RoundRobinSelector implements Selector {
        private final List<InetSocketAddress> serviceAddressList;
        private final int capacity;
        private int cursor;

        public RoundRobinSelector(List<InetSocketAddress> serviceAddressList) {
            if (serviceAddressList == null || serviceAddressList.size() == 0) {
                log.error("负载均衡发现当前服务列表为空");
                throw new LoadBalanceException("负载均衡发现当前服务列表为空");
            }
            this.serviceAddressList = serviceAddressList;
            this.cursor = 0;
            this.capacity = serviceAddressList.size();
        }


        @Override
        public InetSocketAddress next() {
            synchronized (this) {
                return serviceAddressList.get(cursor++ % capacity);
            }
        }
    }
}
