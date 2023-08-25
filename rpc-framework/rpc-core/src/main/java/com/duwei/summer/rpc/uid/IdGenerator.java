package com.duwei.summer.rpc.uid;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-24 10:48
 * @since: 1.0
 */
public interface IdGenerator {
    long nextId();

    void init(IdGeneratorConfig idGeneratorConfig);
}
