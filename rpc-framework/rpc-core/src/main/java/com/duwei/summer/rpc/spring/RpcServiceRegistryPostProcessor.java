package com.duwei.summer.rpc.spring;

import com.duwei.summer.rpc.Bootstrap;
import com.duwei.summer.rpc.annotation.RpcService;
import com.duwei.summer.rpc.config.ServiceConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.List;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-29 19:39
 * @since: 1.0
 */
public class RpcServiceRegistryPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            List<ServiceConfig<?>> serviceConfigs = ServiceConfig.build(bean);
            Bootstrap.getInstance().publish(serviceConfigs);
        }
        return bean;
    }

}
