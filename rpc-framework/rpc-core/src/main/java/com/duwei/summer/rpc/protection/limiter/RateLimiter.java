package com.duwei.summer.rpc.protection.limiter;

import com.duwei.summer.rpc.transport.message.request.RpcRequest;

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
    boolean allow();
}
