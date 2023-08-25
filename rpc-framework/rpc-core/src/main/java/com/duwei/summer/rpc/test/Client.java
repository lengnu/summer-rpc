package com.duwei.summer.rpc.test;

import com.duwei.summer.rpc.Bootstrap;
import com.duwei.summer.rpc.config.ReferenceConfig;
import com.duwei.summer.rpc.config.ServiceConfig;
import com.duwei.summer.rpc.loadbalance.LoadBalancerConfigs;
import com.duwei.summer.rpc.registry.RegistryConfig;
import com.duwei.summer.rpc.registry.RegistryConfigs;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Ref;
import java.util.List;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-25 17:49
 * @since: 1.0
 */
public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {
        ReferenceConfig<IUser> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setInterfaceRef(IUser.class);
        referenceConfig.setGroup("default");

        Bootstrap instance = Bootstrap.getInstance();
        instance.load("config.xml").reference(referenceConfig);
        Thread.sleep(6 * 1000);

        referenceConfig.get().say();

    }
}
