package com.duwei.summer.rpc.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 服务配置
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-21 21:54
 * @since: 1.0
 */
@Data
@Builder
public class ServiceConfig<T> {
    private Class<?> interfaceProvider;
    private T ref;
    private String group = "default";
}
