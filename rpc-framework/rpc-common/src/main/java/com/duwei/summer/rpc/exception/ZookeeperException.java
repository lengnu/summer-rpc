package com.duwei.summer.rpc.exception;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 16:30
 * @since: 1.0
 */
public class ZookeeperException extends RuntimeException{
    public ZookeeperException() {
    }

    public ZookeeperException(String message) {
        super(message);
    }

    public ZookeeperException(Throwable cause) {
        super(cause);
    }
}
