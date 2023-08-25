package com.duwei.summer.rpc.registry;

import com.duwei.summer.rpc.registry.zk.ZookeeperRegistry;

/**
 * <p>
 *  提供便捷创建内置的注册中心配置类
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-25 19:09
 * @since: 1.0
 */
public class RegistryConfigs {
    private RegistryConfigs(){}

    public static RegistryConfig newZookeeperRegistryConfig(String host,int port){
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setHost(host);
        registryConfig.setPort(port);
        registryConfig.setRegistryClass(ZookeeperRegistry.class);
        return registryConfig;
    }
}
