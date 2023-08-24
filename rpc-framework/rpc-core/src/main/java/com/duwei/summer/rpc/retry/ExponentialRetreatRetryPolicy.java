package com.duwei.summer.rpc.retry;

/**
 * <p>
 * 二进制指数退避策略
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-24 16:20
 * @since: 1.0
 */
public class ExponentialRetreatRetryPolicy extends AbstractRetryPolicy {
    private int base;
    private static final int POW = 2;

    public ExponentialRetreatRetryPolicy(int retryTimes, int tickTimes) {
        super(retryTimes, tickTimes);
        base = 1;
    }

    @Override
    protected int waitInternalTime() {
        int interval = base * tickTimes;
        base *= POW;
        return interval;
    }
}
