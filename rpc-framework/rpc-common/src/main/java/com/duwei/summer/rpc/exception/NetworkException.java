package com.duwei.summer.rpc.exception;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 17:02
 * @since: 1.0
 */
public class NetworkException extends RuntimeException{
    public NetworkException() {
    }

    public NetworkException(Throwable cause) {
        super(cause);
    }

    public NetworkException(String message) {
        super(message);
    }
}
