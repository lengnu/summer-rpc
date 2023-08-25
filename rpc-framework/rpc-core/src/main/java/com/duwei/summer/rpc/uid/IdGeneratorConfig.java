package com.duwei.summer.rpc.uid;

import com.duwei.summer.rpc.config.BaseConfig;
import com.duwei.summer.rpc.exception.RegistryException;
import com.duwei.summer.rpc.loadbalance.LoadBalancer;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-25 20:38
 * @since: 1.0
 */
@Slf4j
public class IdGeneratorConfig extends BaseConfig {
    private volatile IdGenerator idGenerator;
    private Class<? extends IdGenerator> idGeneratorClass;

    public void setIdGeneratorClass(Class<? extends IdGenerator> idGeneratorClass) {
        this.idGeneratorClass = idGeneratorClass;
    }

    public long nextId() {
        return getIdGenerator().nextId();
    }

    public IdGenerator getIdGenerator() {
        if (idGenerator == null) {
            synchronized (this) {
                if (idGenerator == null) {
                    try {
                        idGenerator = idGeneratorClass.getConstructor().newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        log.error("ID生成器初始化异常，必须提供可访问的无参构造器");
                        throw new RegistryException("D生成器初始化异常，必须提供可访问的无参构造器");
                    }
                    idGenerator.init(this);
                }
            }
        }
        return idGenerator;
    }
}
