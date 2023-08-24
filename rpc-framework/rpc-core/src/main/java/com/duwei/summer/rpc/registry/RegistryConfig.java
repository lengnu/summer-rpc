package com.duwei.summer.rpc.registry;

import com.duwei.summer.rpc.constant.Constant;
import com.duwei.summer.rpc.exception.RegistryException;
import com.duwei.summer.rpc.registry.zk.ZookeeperRegistry;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *  注册中心配置，根据配置的注册中心连接地址初始化注册中心
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-21 21:49
 * @since: 1.0
 */
@Data
@NoArgsConstructor
public class RegistryConfig {
    /**
     * 连接的URL，需要前缀（协议名称）
     */
    private String connectString;
    private String username;
    private String password;
    private Registry registry;
    private String registryType;

    private static final String ZOOKEEPER = "zookeeper";

    public RegistryConfig(String connectString) {
         registryType = getRegistryType(connectString, true);
         connectString = getRegistryType(connectString, false);
    }

    public void init(){
//        if (ZOOKEEPER.equalsIgnoreCase(registryType)) {
//            registry = new ZookeeperRegistry(connectStr, Constant.TIMEOUT);
//        }
        throw new RegistryException("未找到使用该类型协议的注册中心");
    }


    public Registry getRegistry() {
        return registry;
    }

    /**
     * 根据连接地址获取注册中心的协议
     *
     * @param connectString 连接地址
     * @return 注册中心地址
     */
    private String getRegistryType(String connectString, boolean ifType) {
        String[] typeAndHost = connectString.split("://");
        if (typeAndHost.length != 2) {
            throw new RegistryException("给定的注册中心Url非法");
        }
        if (ifType) {
            return typeAndHost[0];
        }
        return typeAndHost[1];
    }
}
