package com.duwei.summer.rpc.loadbalance;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * <p>
 * 负载均衡器-顶级接口
 * 内部维护一个服务列表
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-23 15:25
 * @since: 1.0
 */
public interface LoadBalancer {
    /**
     * 根据服务名获取一个可用服务
     *
     * @param serviceName 服务名称
     * @param group 服务组
     * @return 可用服务
     */
    InetSocketAddress selectServiceAddress(String serviceName,String group);

    /**
     * 更新服务列表
     * @param serviceName   服务名称
     * @param serviceAddressList    服务i列表
     */
    void updateServiceList(String serviceName, List<InetSocketAddress> serviceAddressList);


    /**
     * 获取某个服务所有可用的地址
     * @param serviceName   服务名称
     * @return  该服务下所有可用服务提供者的地址
     */
    List<InetSocketAddress> getServiceAddress(String serviceName);

    /**
     * 初始化
     * @param loadBalancerConfig    负载均衡的参数配置
     */
    void init(LoadBalancerConfig loadBalancerConfig);
}
