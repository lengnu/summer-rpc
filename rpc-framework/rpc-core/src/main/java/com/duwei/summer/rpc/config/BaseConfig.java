package com.duwei.summer.rpc.config;

import com.duwei.summer.rpc.context.ApplicationContext;
import com.duwei.summer.rpc.context.ApplicationContextAware;
import com.duwei.summer.rpc.property.AttributeAccessorSupport;

/**
 * <p>
 * 基础配置类
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-25 19:17
 * @since: 1.0
 */
public class BaseConfig extends AttributeAccessorSupport implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext application) {
        this.applicationContext = application;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }


}
