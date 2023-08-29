package com.duwei.summer.rpc.transport.message.response;

import com.duwei.summer.rpc.transport.message.request.RpcRequest;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-29 15:37
 * @since: 1.0
 */
public class ResponseHelper {
    public static RpcResponse buildHeartBeatResponse(RpcRequest rpcRequest) {
        return RpcResponse.builder()
                .code(ResponseCode.SUCCESS_HEART_BEAT.getCode())
                .timeStamp(rpcRequest.getTimeStamp())
                .build();
    }
}
