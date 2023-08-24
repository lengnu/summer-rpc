package com.duwei.summer.rpc.protection;


import com.duwei.summer.rpc.transport.message.request.RpcRequest;

/**
 * <p>
 * 基于令牌桶算法的限流器
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-24 17:03
 * @since: 1.0
 */
public class TokenBuketRateLimiter implements RateLimiter {
    /**
     * 令牌数量
     */
    private long tokens;
    /**
     * 令牌桶总流量
     */
    private final long capacity;
    /**
     * 给桶中加入令牌的速率，单位为 s
     */
    private final long rate;
    /**
     * 上一次放令牌的时间
     */
    private long lastPutTokenTime = System.currentTimeMillis();
    /**
     * 放令牌的最小间隔
     */
    private final long putTokenMinInternalTime;

    public TokenBuketRateLimiter(long capacity, long rate) {
        this.capacity = capacity;
        this.tokens = capacity;
        this.rate = rate;
        this.putTokenMinInternalTime = 1000 / rate;
    }

    /**
     * 判断请求是否可用进行放行
     *
     * @return true:放行 false:拦截
     */
    @Override
    public synchronized boolean allow(RpcRequest rpcRequest) {
        //1. 给令牌桶添加令牌
        long currentTime = System.currentTimeMillis();
        long timeInterval = currentTime - lastPutTokenTime;
        if (timeInterval >= putTokenMinInternalTime) {
            long addTokens = (long) (timeInterval * rate / 1000.0);
            tokens = Math.min(capacity, addTokens + tokens);
            lastPutTokenTime = System.currentTimeMillis();
        }

        // 2.如果令牌桶中有令牌则放行
        if (tokens > 0) {
            tokens--;
            return true;
        }
        return false;
    }


}
