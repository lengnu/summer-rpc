package com.duwei.summer.rpc.registry;

import com.duwei.summer.rpc.property.AttributeAccessorSupport;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 抽象注册中心接口，提供模板方法
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 17:09
 * @since: 1.0
 */

@Data
@NoArgsConstructor
public abstract class AbstractRegistry implements Registry {
    private RegistryConfig registryConfig;

    @Override
    public void init(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
        this.init();
    }

    /**
     * 子类的初始化钩子方法
     */
    protected abstract void init();
}
