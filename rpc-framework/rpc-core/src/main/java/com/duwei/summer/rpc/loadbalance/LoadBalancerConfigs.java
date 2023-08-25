package com.duwei.summer.rpc.loadbalance;

import com.duwei.summer.rpc.loadbalance.impl.ConsistentHashLoadBalancer;
import com.duwei.summer.rpc.loadbalance.impl.RoundRobinLoadBalancer;

/**
 * <p>
 *  提供简洁化的负载均衡配置
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-25 19:27
 * @since: 1.0
 */
public class LoadBalancerConfigs {

    public static LoadBalancerConfig newRoundRobinLoadBalancerConfig(){
        LoadBalancerConfig loadBalancerConfig = new LoadBalancerConfig();
        loadBalancerConfig.setLoadbalancerClass(RoundRobinLoadBalancer.class);
        return loadBalancerConfig;
    }

    public static LoadBalancerConfig newConsistentHashLoadBalancerConfig(int virtualNodeCount){
        LoadBalancerConfig loadBalancerConfig = new LoadBalancerConfig();
        loadBalancerConfig.setLoadbalancerClass(RoundRobinLoadBalancer.class);
        loadBalancerConfig.setAttribute(ConsistentHashLoadBalancer.VIRTUAL_NODE_NUMBER,virtualNodeCount);
        return loadBalancerConfig;
    }
}
