package com.duwei.summer.rpc.loadbalance;

import com.duwei.summer.rpc.Bootstrap;
import com.duwei.summer.rpc.context.ApplicationContext;

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
     * @return 可用服务
     */
    InetSocketAddress selectServiceAddress(String serviceName);

    /**
     * 更新服务列表
     * @param serviceName   服务名称
     * @param serviceAddressList    服务i列表
     */
    void updateServiceList(String serviceName, List<InetSocketAddress> serviceAddressList);

    /**
     * 设置上下文信息
     * @param application 上下文信息
     */
    void setApplication(ApplicationContext application);
}
