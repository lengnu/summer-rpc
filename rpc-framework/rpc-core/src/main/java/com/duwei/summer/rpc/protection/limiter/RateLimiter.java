package com.duwei.summer.rpc.protection.limiter;


/**
 * <p>
 *  限流器接口
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-24 18:37
 * @since: 1.0
 */
public interface RateLimiter {
    /**
     * 是否允许请求通过
     * @return true 通过 false 阻挡
     */
    boolean allow();
}
