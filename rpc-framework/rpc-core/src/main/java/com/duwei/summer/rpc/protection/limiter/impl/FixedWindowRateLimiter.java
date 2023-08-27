package com.duwei.summer.rpc.protection.limiter.impl;

import com.duwei.summer.rpc.protection.limiter.RateLimiter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 * 固定窗口限流算法
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-27 17:28
 * @since: 1.0
 */
public class FixedWindowRateLimiter implements RateLimiter {
    private final long capacity;
    private final AtomicLong counter = new AtomicLong(0);
    private volatile boolean limit;


    public FixedWindowRateLimiter(long capacity, long window) {
        this.capacity = capacity;
        Thread workerThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    Thread.sleep(window);
                    counter.set(0L);
                    limit = true;
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
        }, "后台重置线程");
        workerThread.start();
    }


    @Override
    public boolean allow() {
        while (true) {
            if (limit) {
                return false;
            }
            long curCount = counter.get();
            if (curCount == capacity) {
                limit = true;
                return false;
            }
            if (counter.compareAndSet(curCount, curCount + 1)) {
                return true;
            }
        }
    }

}
