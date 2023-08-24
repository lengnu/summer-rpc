package com.duwei.summer.rpc.retry;

/**
 * <p>
 *  以固定的时间间隔进行重试
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-24 16:18
 * @since: 1.0
 */
public class FixedIntervalRetryPolicy extends AbstractRetryPolicy{
    public FixedIntervalRetryPolicy(int retryTimes, int tickTimes) {
        super(retryTimes, tickTimes);
    }

    @Override
    protected int waitInternalTime() {
        return tickTimes;
    }
}
