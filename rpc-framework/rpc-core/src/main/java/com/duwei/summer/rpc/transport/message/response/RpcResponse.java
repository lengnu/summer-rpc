package com.duwei.summer.rpc.transport.message.response;

import com.duwei.summer.rpc.context.ApplicationContext;
import com.duwei.summer.rpc.transport.message.request.RpcRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *  响应消息
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-23 11:26
 * @since: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcResponse {
    /**
     * 对应请求ID
     */
    private long requestId;
    /**
     * 压缩类型
     */
    private byte compressedType;
    /**
     * 序列化类型
     */
    private byte serializeType;
    /**
     * 响应时间戳
     */
    private long timeStamp;
    /**
     * 响应码类型
     */
    private byte code;
    /**
     * 响应结果
     */
    private Object body;

    public static RpcResponse build(RpcRequest rpcRequest,ResponseCode responseCode,Object body){
        return builder()
                .code(responseCode.getCode())
                .compressedType(rpcRequest.getCompressedType())
                .serializeType(rpcRequest.getSerializeType())
                .timeStamp(System.currentTimeMillis())
                .requestId(rpcRequest.getRequestId())
                .body(body)
                .build();
    }
}
