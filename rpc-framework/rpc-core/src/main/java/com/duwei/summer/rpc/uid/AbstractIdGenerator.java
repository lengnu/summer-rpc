package com.duwei.summer.rpc.uid;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-25 20:59
 * @since: 1.0
 */
public abstract class AbstractIdGenerator implements IdGenerator{
    private IdGeneratorConfig idGeneratorConfig;

    @Override
    public void init(IdGeneratorConfig idGeneratorConfig) {
        this.idGeneratorConfig = idGeneratorConfig;
        this.init();
    }

    protected abstract void init();

    public IdGeneratorConfig getIdGeneratorConfig() {
        return idGeneratorConfig;
    }
}
