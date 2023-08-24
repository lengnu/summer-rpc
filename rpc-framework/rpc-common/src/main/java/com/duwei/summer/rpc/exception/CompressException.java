package com.duwei.summer.rpc.exception;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-23 09:57
 * @since: 1.0
 */
public class CompressException extends RuntimeException{

    public CompressException() {
    }

    public CompressException(String message) {
        super(message);
    }

    public CompressException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompressException(Throwable cause) {
        super(cause);
    }
}
