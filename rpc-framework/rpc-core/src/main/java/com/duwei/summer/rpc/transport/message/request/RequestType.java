package com.duwei.summer.rpc.transport.message.request;

/**
 * <p>
 *  请求类型枚举
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-23 11:17
 * @since: 1.0
 */
public enum RequestType {
    /**
     * 普通请求
     */
    COMMON_REQUEST((byte) 1,"common request"),
    /**
     * 心跳检测请求
     */
    HEART_BEAT((byte) 2,"heart beat request");
    private final byte id;
    private final String type;

    RequestType(byte id, String type) {
        this.id = id;
        this.type = type;
    }

    public byte getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
