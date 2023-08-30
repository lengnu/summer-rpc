package com.duwei.summer.rpc.loadbalance.impl;

import com.duwei.summer.rpc.Bootstrap;
import com.duwei.summer.rpc.loadbalance.AbstractLoadBalancer;
import com.duwei.summer.rpc.loadbalance.Selector;
import com.duwei.summer.rpc.transport.ChannelProvider;

import java.net.InetSocketAddress;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-30 09:12
 * @since: 1.0
 */
public class MinimumResponseTimeLoadBalancer extends AbstractLoadBalancer {
    @Override
    protected void init() {
    }

    @Override
    protected Selector getSelector(String serviceName, List<InetSocketAddress> serviceAddressList) {
        ChannelProvider channelProvider = Bootstrap.getInstance().getApplicationContext().getChannelProvider();
        return new MinimumResponseTimeSelector(channelProvider,serviceAddressList);
    }

    public static class MinimumResponseTimeSelector implements Selector {
        private ChannelProvider channelProvider;
        private List<InetSocketAddress> serviceAddressList;

        public MinimumResponseTimeSelector(ChannelProvider channelProvider, List<InetSocketAddress> serviceAddressList) {
            this.channelProvider = channelProvider;
            this.serviceAddressList = serviceAddressList;
        }

        @Override
        public InetSocketAddress next() {
            // TODO 有问题需要优化 缓存 -> 动态加强堆优化
            Optional<InetSocketAddress> first = channelProvider.getResponseTimeCache().entrySet().stream()
                    .filter(serviceAddressList::contains)
                    .sorted((o1, o2) -> (int) (o1.getValue() - o2.getValue()))
                    .map(Map.Entry::getKey)
                    .findFirst();
           return first.orElseGet(() -> serviceAddressList.get(0));
        }

        @Override
        public List<InetSocketAddress> getAll() {
            return serviceAddressList;
        }
    }

}
