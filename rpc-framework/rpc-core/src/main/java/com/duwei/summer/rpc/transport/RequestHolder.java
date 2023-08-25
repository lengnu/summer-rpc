package com.duwei.summer.rpc.transport;

import com.duwei.summer.rpc.transport.message.request.RpcRequest;

/**
 * <p>
 * 记录当前线程上下文处理的对象
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-23 16:23
 * @since: 1.0
 */
public class RequestHolder {
    private  final ThreadLocal<RpcRequest> requestThreadLocalCache = new ThreadLocal<>();

    public  RpcRequest getRpcRequest() {
        return requestThreadLocalCache.get();
    }

    public  void setRpcRequest(RpcRequest rpcRequest) {
        requestThreadLocalCache.set(rpcRequest);
    }

    public  void removeRpcRequest() {
        requestThreadLocalCache.remove();
    }
}
