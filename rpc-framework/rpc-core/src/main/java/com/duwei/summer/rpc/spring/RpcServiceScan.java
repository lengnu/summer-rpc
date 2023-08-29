package com.duwei.summer.rpc.spring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-29 19:24
 * @since: 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ImportServiceSelector.class)
public @interface RpcServiceScan {
    String[] basePackages();
}
