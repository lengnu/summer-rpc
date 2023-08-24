package com.duwei.summer.rpc.annotation;

import com.duwei.summer.rpc.retry.ExponentialRetreatRetryPolicy;
import com.duwei.summer.rpc.retry.RetryPolicy;

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
 * @date: 2023-08-24 16:11
 * @since: 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retry {
    /**
     * 重试次数
     */
    int retryTimes() default 3;

    /**
     * 基础重试单位ms
     */
    int tickTimes() default 200;

    /**
     * 重试策略
     */
    Class<? extends RetryPolicy> retryPolicy() default ExponentialRetreatRetryPolicy.class;
}
