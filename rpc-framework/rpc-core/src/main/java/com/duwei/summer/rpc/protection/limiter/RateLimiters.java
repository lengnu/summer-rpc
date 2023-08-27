package com.duwei.summer.rpc.protection.limiter;

import com.duwei.summer.rpc.protection.limiter.impl.TokenBuketRateLimiter;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-27 17:22
 * @since: 1.0
 */
public class RateLimiters {
    private RateLimiters(){

    }

    public static RateLimiter newTokenBucketRateLimiter(long maxPermits,long permitPerSecond){
        return new TokenBuketRateLimiter(maxPermits,permitPerSecond);
    }

    public static RateLimiter newTokenBucketRateLimiter( ){
        return new TokenBuketRateLimiter();
    }
}
