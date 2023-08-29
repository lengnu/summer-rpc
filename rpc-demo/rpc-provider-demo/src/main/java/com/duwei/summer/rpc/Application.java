package com.duwei.summer.rpc;


import com.duwei.summer.rpc.config.ServiceConfig;
import com.duwei.summer.rpc.registry.RegistryConfig;
import com.duwei.summer.rpc.spring.RpcServiceScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 16:35
 * @since: 1.0
 */

public class Application {
    public static void main(String[] args) throws IOException {
        Bootstrap.getInstance().scanService("com.duwei.summer.rpc.impl").start();
    }
}
