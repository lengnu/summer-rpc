package com.duwei.summer.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 需要被发布的服务
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-24 10:24
 * @since: 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {
    String name() default "";

    String group() default "default";
}
