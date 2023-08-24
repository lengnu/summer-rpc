package com.duwei.summer.rpc.discovery;

import com.duwei.summer.rpc.Bootstrap;
import com.duwei.summer.rpc.registry.Registry;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


import java.lang.reflect.Proxy;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-21 22:00
 * @since: 1.0
 */
@Slf4j
@Data
public class ReferenceConfig<T> {
    private Class<T> interfaceRef;
    private Registry registry;
    private Bootstrap bootstrap;

    @SuppressWarnings("unchecked")
    public T get() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?>[] classes = {interfaceRef};
       // return (T) Proxy.newProxyInstance(classLoader, classes, new RpcConsumerInvocationHandler(registry, interfaceRef,bootstrap));
        return null;
    }
}
