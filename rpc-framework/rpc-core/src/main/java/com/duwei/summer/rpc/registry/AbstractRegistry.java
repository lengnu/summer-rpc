package com.duwei.summer.rpc.registry;

import com.duwei.summer.rpc.compress.ServiceConfig;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * <p>
 *  抽象注册中心接口，提供模板方法
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 17:09
 * @since: 1.0
 */

public class AbstractRegistry implements Registry{
    @Override
    public void registry(ServiceConfig<?> serviceConfig) {

    }

    @Override
    public List<InetSocketAddress> lookup(String serviceName) {
        return null;
    }
}
