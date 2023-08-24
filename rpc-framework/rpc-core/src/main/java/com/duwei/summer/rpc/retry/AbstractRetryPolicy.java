package com.duwei.summer.rpc.retry;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-24 16:16
 * @since: 1.0
 */
public abstract class AbstractRetryPolicy implements RetryPolicy{
    protected int retryTimes;
    protected int tickTimes;

    public AbstractRetryPolicy(int retryTimes, int tickTimes) {
        this.retryTimes = retryTimes;
        this.tickTimes = tickTimes;
    }

    @Override
    public  int nextIntervalTime(){
        int intervalTime = waitInternalTime();
        retryTimes--;
        return intervalTime;
    }


    protected abstract int waitInternalTime();

    @Override
    public boolean hasRetryTimes() {
        return retryTimes != 0;
    }
}
