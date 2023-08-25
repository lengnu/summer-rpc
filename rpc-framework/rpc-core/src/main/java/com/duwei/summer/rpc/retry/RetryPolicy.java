package com.duwei.summer.rpc.retry;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-24 16:12
 * @since: 1.0
 */
public interface RetryPolicy {
    /**
     * 获取下一次的重试间隔
     * @return  重试间隔时间
     */
    int nextIntervalTime();

    /**
     * 是否还有剩余重试次数
     * @return 为0则不可重试
     */
    boolean hasRetryTimes();

    /**
     * 默认重试次数
     */
    int DEFAULT_RETRY_TIMES = 3;
    /**
     * 默认重试间隔
     */
    int DEFAULT_RETRY_INTERVAL = 200;

}
