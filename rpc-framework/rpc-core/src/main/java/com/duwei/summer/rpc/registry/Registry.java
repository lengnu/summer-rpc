package com.duwei.summer.rpc.registry;

import com.duwei.summer.rpc.config.ServiceConfig;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * <p>
 * 服务注册接口
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 17:06
 * @since: 1.0
 */
public interface Registry {
    /**
     * 注册服务
     * @param serviceConfig 服务配置
     */
    void registry(ServiceConfig<?> serviceConfig);

    /**
     * 服务发现
     * @param serviceName   服务名称
     * @param group 分组
     * @return  服务列表
     */
    List<InetSocketAddress> lookup(String serviceName,String group);


    /**
     * 根据配置项初始化出则中心
     * @param registryConfig 配置项
     */
    void init(RegistryConfig registryConfig);
}
