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
    SUCCESS((byte) 20,"服务调用成功"),
    /**
     * 心跳
     */
    SUCCESS_HEART_BEAT((byte) 2,"心跳检测成功"),
    /**
     * 被限流
     */
    RATE_LIMIT((byte) 31,"服务端正在执行限流"),
    /**
     * 客户端错误
     */
    CLIENT_ERROR((byte) 40,"客户端发送的请求出错"),
    /**
     * 服务端错误
     */
    SERVER_ERROR((byte) 50,"服务器内部执行错误"),
    /**
     * 正在关闭
     */
    SERVER_CLOSING((byte) 60,"服务器正在关闭，无法处理请求"),
    /**
     * 响应无效
     */
    INVALID((byte) 0,"无效的响应码");

    private final byte code;
    private final String desc;


    ResponseCode(byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public byte getCode() {
        return code;
    }

    public String getDesc(){
        return desc;
    }


}
