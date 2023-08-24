package com.duwei.summer.rpc.transport.message.response;

/**
 * <p>
 * 20 成功 21 心跳
 * 31 负载过高，现楼
 * 40 客户端错误
 * 50 服务端错误
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-23 11:28
 * @since: 1.0
 */
public enum ResponseCode {
    /**
     * 成功
     */
    SUCCESS((byte) 20),
    /**
     * 心跳
     */
    SUCCESS_HEART_BEAT((byte) 2),
    /**
     * 被限流
     */
    RATE_LIMIT((byte) 31),
    /**
     * 客户端错误
     */
    CLIENT_ERROR((byte) 40),
    /**
     * 服务端错误
     */
    SERVER_ERROR((byte) 50);

    private final int code;


    ResponseCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }


}
