package com.duwei.summer.rpc.transport.handler.service;

import com.duwei.summer.rpc.context.ApplicationContext;
import com.duwei.summer.rpc.context.ApplicationContextAware;
import com.duwei.summer.rpc.transport.message.request.RequestHelper;
import com.duwei.summer.rpc.transport.message.request.RequestType;
import com.duwei.summer.rpc.transport.message.request.RpcRequest;
import com.duwei.summer.rpc.transport.message.response.ResponseCode;
import com.duwei.summer.rpc.transport.message.response.ResponseHelper;
import com.duwei.summer.rpc.transport.message.response.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 请求挡板，确定是否可用放行
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-28 09:16
 * @since: 1.0
 */
@Slf4j
public class BaffleHandler extends SimpleChannelInboundHandler<RpcRequest> implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        if ((rpcRequest.getRequestType() == RequestType.HEART_BEAT.getId())) {
            if (log.isDebugEnabled()) {
                log.debug("接收到客户端{}的心跳检测请求", ctx.channel().remoteAddress());
            }
            RpcResponse rpcResponse = ResponseHelper.buildHeartBeatResponse(rpcRequest);
            ctx.channel().writeAndFlush(rpcResponse);
            return;
        } else if (!applicationContext.isActive()) {
            RpcResponse rpcResponse = RpcResponse.build(rpcRequest, ResponseCode.SERVER_CLOSING, null);
            ctx.channel().writeAndFlush(rpcResponse);
            return;
        } else if (!applicationContext.allowRequest()) {
            if (log.isDebugEnabled()) {
                log.debug("服务端正在进行限流，无法处理客户端请求");
            }
            RpcResponse rpcResponse = RpcResponse.build(rpcRequest, ResponseCode.RATE_LIMIT, null);
            ctx.channel().writeAndFlush(rpcResponse);
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("接收到客户端请求，请求数加1");
        }
        applicationContext.countRequest();
        ctx.fireChannelRead(rpcRequest);
    }


    @Override
    public void setApplicationContext(ApplicationContext application) {
        this.applicationContext = application;
    }
}
