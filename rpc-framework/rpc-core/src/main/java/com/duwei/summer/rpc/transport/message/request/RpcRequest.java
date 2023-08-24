package com.duwei.summer.rpc.transport.message.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *  服务调用方发起的请求内容
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 19:50
 * @since: 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest {
    /**
     * 请求ID
     */
    private long requestId;
    /**
     * 压缩类型
     */
    private byte compressedType;
    /**
     * 请求类型
     */
    private byte requestType;
    /**
     * 序列化类型
     */
    private byte serializeType;
    /**
     * 请求时间戳
     */
    private long timeStamp;
    /**
     * 请求负载
     */
    private RequestPayload requestPayload;
}
