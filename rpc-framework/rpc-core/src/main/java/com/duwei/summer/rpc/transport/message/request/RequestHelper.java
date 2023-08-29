package com.duwei.summer.rpc.transport.message.request;

import com.duwei.summer.rpc.context.ApplicationContext;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-29 15:06
 * @since: 1.0
 */
public class RequestHelper {
    private RequestHelper(){}

    public static RpcRequest buildHeartBeatRequest(){
       return RpcRequest.builder()
                .requestType(RequestType.HEART_BEAT.getId())
                .timeStamp(System.currentTimeMillis())
                .build();

    }
}
