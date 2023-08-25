package com.duwei.summer.rpc.uid;

import com.duwei.summer.rpc.uid.impl.SnowflakeIdGenerator;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-25 21:09
 * @since: 1.0
 */
public class IdGeneratorConfigs {
    public static IdGeneratorConfig newSnowflakeIdGeneratorConfig(long dataCenterId, long workerId) {
        IdGeneratorConfig idGeneratorConfig = new IdGeneratorConfig();
        idGeneratorConfig.setIdGeneratorClass(SnowflakeIdGenerator.class);
        idGeneratorConfig.setAttribute(SnowflakeIdGenerator.DATA_CENTER_ID, dataCenterId);
        idGeneratorConfig.setAttribute(SnowflakeIdGenerator.WORKER_ID, workerId);
        return idGeneratorConfig;
    }
}
