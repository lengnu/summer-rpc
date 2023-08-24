package com.duwei.summer.rpc.protection;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 断路器
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-24 17:30
 * @since: 1.0
 */
public class CircuitBreaker {
    private volatile Status status;

    private AtomicInteger requestCount = new AtomicInteger();
    private AtomicInteger errorRequestCount = new AtomicInteger();
    private int maxErrorRequestCount;
    private int maxErrorRequestRate;
    private double threshold;


    public void recordRequest() {
        requestCount.getAndIncrement();
    }

    public void recordException(){
        requestCount.getAndIncrement();
    }

    public boolean isBreak(){
        if (status == Status.OPEN){

        }
        return true;
    }

    public void reset(){

    }


    public static enum Status {
        OPEN,

    }
}
