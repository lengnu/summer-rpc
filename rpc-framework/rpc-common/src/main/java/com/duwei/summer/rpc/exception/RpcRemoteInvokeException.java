package com.duwei.summer.rpc.exception;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-25 22:10
 * @since: 1.0
 */
public class RpcRemoteInvokeException extends RuntimeException{
    public RpcRemoteInvokeException() {
    }

    public RpcRemoteInvokeException(String message) {
        super(message);
    }

    public RpcRemoteInvokeException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcRemoteInvokeException(Throwable cause) {
        super(cause);
    }
}
