package com.duwei.summer.rpc.protection.breaker;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;


/**
 * @author duwei
 */
@Slf4j
public class CircuitBreaker {
    private static final int DEFAULT_FAILURE_THRESHOLD = 3;
    private static final Duration DEFAULT_RESET_DURATION = Duration.of(300L, ChronoUnit.MILLIS);

    /**
     * 连续失败阈值
     */
    private final int failureThreshold;
    /**
     * 打开状态后的重置时间
     */
    private final Duration resetTimeout;
    /**
     * 记录当前状态
     */
    private final AtomicReference<CircuitBreakerState> state;
    /**
     * 上次失败时间
     */
    private Instant lastFailureTime;
    /**
     * 连续失败次数
     */
    private int consecutiveFailures;
    /**
     * 连续成功次数
     */
    private int consecutiveSuccesses;
    private final ScheduledExecutorService executorService;
    private ScheduledFuture<?> resetFuture;

    public CircuitBreaker() {
        this(DEFAULT_FAILURE_THRESHOLD, DEFAULT_RESET_DURATION);
    }

    public CircuitBreaker(int failureThreshold, Duration resetTimeout) {
        this.failureThreshold = failureThreshold;
        this.resetTimeout = resetTimeout;
        this.state = new AtomicReference<>(CircuitBreakerState.CLOSED);
        this.lastFailureTime = null;
        this.consecutiveFailures = 0;
        this.consecutiveSuccesses = 0;
        this.executorService = Executors.newScheduledThreadPool(1);
        this.resetFuture = null;
    }

    public synchronized boolean allowRequest() {
        CircuitBreakerState currentState = state.get();
        /*
         * 半开只允许一个请求
         */
        switch (currentState) {
            case HALF_OPEN:
                return consecutiveSuccesses == 0;
            case OPEN:
                return false;
            default:
                return true;
        }
    }

    public synchronized void recordFailure() {
        if (log.isDebugEnabled()){
            log.debug("服务调用失败，记录失败次数");
        }
        CircuitBreakerState currentState = state.get();
        consecutiveFailures++;
        consecutiveSuccesses = 0;
        lastFailureTime = Instant.now();
        switch (currentState) {
            case CLOSED:
                if (consecutiveFailures == failureThreshold) {
                    setHalfOpenState();
                }
                break;
            case HALF_OPEN:
                setOpenState();
                scheduleResetTask();
                break;
            default:
                break;
        }
    }

    public synchronized void recordSuccess() {
        if (log.isDebugEnabled()){
            log.debug("服务调用成功，记录成功次数");
        }
        CircuitBreakerState currentState = state.get();
        consecutiveSuccesses++;
        consecutiveFailures = 0;
        // 如果半开状态下成功切换到关闭状态
        if (currentState == CircuitBreakerState.HALF_OPEN) {
            cancelResetTask();
            setClosedState();
        }
    }

    public void reset() {
        state.set(CircuitBreakerState.CLOSED);
        lastFailureTime = null;
        consecutiveFailures = 0;
        consecutiveSuccesses = 0;
        cancelResetTask();
    }

    private void setOpenState() {
        state.set(CircuitBreakerState.OPEN);
    }

    private void setHalfOpenState() {
        state.set(CircuitBreakerState.HALF_OPEN);
    }

    private void setClosedState() {
        state.set(CircuitBreakerState.CLOSED);
    }

    private void scheduleResetTask() {
        cancelResetTask(); // 取消之前的重置任务
        resetFuture = executorService.schedule(this::setHalfOpenState, resetTimeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    private void cancelResetTask() {
        if (resetFuture != null) {
            resetFuture.cancel(false);
            resetFuture = null;
        }
    }


    public int getFailureThreshold() {
        return failureThreshold;
    }

    public Instant getLastFailureTime() {
        return lastFailureTime;
    }

    public int getConsecutiveFailures() {
        return consecutiveFailures;
    }

    public int getConsecutiveSuccesses() {
        return consecutiveSuccesses;
    }

    public AtomicReference<CircuitBreakerState> getState() {
        return state;
    }

    enum CircuitBreakerState {
        /**
         * 关闭状态，允许请求通过
         */
        CLOSED,
        /**
         * 半开状态，限制部分请求
         */
        HALF_OPEN,
        /**
         * 打开状态，所有请求不允许通过
         */
        OPEN
    }
}