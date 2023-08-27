package com.duwei.summer.rpc.test;

import com.duwei.summer.rpc.Bootstrap;
import com.duwei.summer.rpc.config.ServiceConfig;
import com.duwei.summer.rpc.registry.RegistryConfig;
import com.duwei.summer.rpc.registry.RegistryConfigs;

import java.io.IOException;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-25 17:49
 * @since: 1.0
 */
public class Service {
    public static void main(String[] args) throws IOException, InterruptedException {
        Bootstrap instance = Bootstrap.getInstance();
        User user = new User();
        instance.load("config.xml")
                .publish(ServiceConfig.builder()
                        .group("default")
                        .ref(user)
                        .interfaceProvider(IUser.class)
                        .build())
                .start();
    }
}
