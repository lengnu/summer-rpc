package com.duwei.summer.rpc.context;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 记录服务端请求的相关信息
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-28 09:43
 * @since: 1.0
 */
public class RequestCounter {
    private static final RequestCounter REQUEST_COUNTER = new RequestCounter();

    public static RequestCounter getInstance() {
        return REQUEST_COUNTER;
    }

    private RequestCounter() {
        count = new AtomicInteger(0);
        processing = new AtomicInteger();
    }

    private final AtomicInteger count;
    private final AtomicInteger processing;

    public void count() {
        count.incrementAndGet();
        processing.incrementAndGet();
    }

    public void finish() {
        processing.decrementAndGet();
    }

    public int getProcessingRequest() {
        return processing.get();
    }

    public int getProcessedRequest() {
        return count.get();
    }

}
