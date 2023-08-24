package com.duwei.summer.rpc.retry;

import java.util.Map;

/**
 * <p>
 * 在指定范围内随机进行重试
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-24 16:22
 * @since: 1.0
 */
public class RandomRetryPolicy extends AbstractRetryPolicy {
    public RandomRetryPolicy(int retryTimes, int tickTimes) {
        super(retryTimes, tickTimes);
    }


    @Override
    protected int waitInternalTime() {
        return (int) (tickTimes * Math.random());
    }
}
