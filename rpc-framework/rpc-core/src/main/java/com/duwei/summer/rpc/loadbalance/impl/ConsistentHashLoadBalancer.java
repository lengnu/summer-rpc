package com.duwei.summer.rpc.loadbalance.impl;

import com.duwei.summer.rpc.exception.LoadBalanceException;
import com.duwei.summer.rpc.loadbalance.AbstractLoadBalancer;
import com.duwei.summer.rpc.loadbalance.Selector;
import com.duwei.summer.rpc.transport.message.request.RpcRequest;
import com.duwei.summer.rpc.util.HashUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * <p>
 * 一致性Hash的负载均衡器
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-23 16:09
 * @since: 1.0
 */
@Slf4j
public class ConsistentHashLoadBalancer extends AbstractLoadBalancer {
    public static final String VIRTUAL_NODE_NUMBER = "virtual";
    private static final int DEFAULT_VIRTUAL_NODE_NUMBER = 128;
    private int virtualNodeNumber;


    @Override
    protected void init() {
        Object attribute = getLoadBalancerConfig().getAttribute(VIRTUAL_NODE_NUMBER);
        if (attribute == null){
            virtualNodeNumber = DEFAULT_VIRTUAL_NODE_NUMBER;
        }else {
            this.virtualNodeNumber = Integer.parseInt(attribute.toString());
        }
    }

    @Override
    protected Selector getSelector(String serviceName, List<InetSocketAddress> serviceAddressList) {
        return new ConsistentHashSelector(serviceAddressList, virtualNodeNumber);
    }

    private  class ConsistentHashSelector implements Selector {
        /**
         * Hash环
         */
        private final SortedMap<Integer, InetSocketAddress> circle = new TreeMap<>();
        /**
         * 虚拟节点数量
         */
        private final int virtualNodeNumber;

        private final List<InetSocketAddress> serviceList;


        public ConsistentHashSelector(List<InetSocketAddress> serviceAddressList, int virtualNodeNumber) {
            if (serviceAddressList == null || serviceAddressList.size() == 0) {
                log.error("负载均衡发现当前服务列表为空");
                throw new LoadBalanceException("负载均衡发现当前服务列表为空");
            }
            this.serviceList = serviceAddressList;
            this.virtualNodeNumber = virtualNodeNumber;
            serviceAddressList.forEach(this::addNodeToCircle);
        }

        /**
         * 根据请求信息做负载均衡
         */
        @Override
        public InetSocketAddress next() {
            RpcRequest rpcRequest = getLoadBalancerConfig().getApplicationContext().getRpcRequest();
            String requestId = String.valueOf(rpcRequest.getRequestId());
            int hash = hash(requestId);

            if (!circle.containsKey(hash)) {
                SortedMap<Integer, InetSocketAddress> tailMap = circle.tailMap(hash);
                hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
            }
            return circle.get(hash);
        }

        @Override
        public List<InetSocketAddress> getAll() {
            return this.serviceList;
        }


        /**
         * 将节点挂载在Hash环上
         *
         * @param inetSocketAddress 虚拟地址
         */
        public void addNodeToCircle(InetSocketAddress inetSocketAddress) {
            for (int i = 0; i < virtualNodeNumber; i++) {
                int hash = hash(inetSocketAddress.toString() + "-" + i);
                circle.put(hash, inetSocketAddress);
            }
            if (log.isDebugEnabled()) {
                log.debug("节点{}挂载在Hash环上", inetSocketAddress);
            }
        }
    }

    private static int hash(String data) {
        return HashUtils.hashTo4Byte(data);
    }
}
