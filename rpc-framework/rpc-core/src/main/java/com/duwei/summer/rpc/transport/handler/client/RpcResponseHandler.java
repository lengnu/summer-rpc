package com.duwei.summer.rpc.transport.handler.client;

import com.duwei.summer.rpc.context.ApplicationContext;
import com.duwei.summer.rpc.context.ApplicationContextAware;
import com.duwei.summer.rpc.transport.message.response.ResponseCode;
import com.duwei.summer.rpc.transport.message.response.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-24 09:07
 * @since: 1.0
 */
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse> implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        byte responseCode = rpcResponse.getCode();
        if (responseCode == ResponseCode.RATE_LIMIT.getCode()) {

        }
    }


    @Override
    public void setApplicationContext(ApplicationContext application) {
        this.applicationContext = application;
    }
}
