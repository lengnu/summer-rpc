package com.duwei.summer.rpc.exception;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-27 19:30
 * @since: 1.0
 */
public class CircuitBreakException extends RuntimeException{
    public CircuitBreakException() {
    }

    public CircuitBreakException(String message) {
        super(message);
    }
}
