package com.duwei.summer.rpc.transport.handler.client;

import com.duwei.summer.rpc.context.ApplicationContext;
import com.duwei.summer.rpc.context.ApplicationContextAware;
import com.duwei.summer.rpc.exception.ResponseException;
import com.duwei.summer.rpc.transport.message.response.ResponseCode;
import com.duwei.summer.rpc.transport.message.response.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-24 09:07
 * @since: 1.0
 */
@Slf4j
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse> implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        long requestId = rpcResponse.getRequestId();
        byte responseCode = rpcResponse.getCode();
        if (responseCode == ResponseCode.SUCCESS_HEART_BEAT.getCode()) {
            processHeartBeatResponse(channelHandlerContext, rpcResponse);
            return;
        }

        Object res = null;
        Exception exception = null;
        try {
            if (responseCode == ResponseCode.RATE_LIMIT.getCode()) {
                log.error("当前ID为{}被限流", rpcResponse.getRequestId());
                exception = new ResponseException(ResponseCode.RATE_LIMIT.getDesc());
            } else if (responseCode == ResponseCode.SUCCESS.getCode()) {
                if (log.isDebugEnabled()) {
                    log.debug("ID为{}的请求执行成功", rpcResponse.getRequestId());
                    res = rpcResponse.getBody();
                }
            } else if (responseCode == ResponseCode.CLIENT_ERROR.getCode()) {
                log.error("ID为{}的客户端请求体异常", rpcResponse.getRequestId());
                exception = new ResponseException(ResponseCode.CLIENT_ERROR.getDesc());
            } else if (responseCode == ResponseCode.SERVER_ERROR.getCode()) {
                log.error("ID为{}的请求服务端执行错误", rpcResponse.getRequestId());
                exception = new ResponseException(ResponseCode.SERVER_ERROR.getDesc());
            } else {
                log.error("无效的响应码");
                exception = new ResponseException(ResponseCode.INVALID.getDesc());
            }
        } finally {
            if (res != null){
                applicationContext.completeResponse(requestId,res);
            }else {
                applicationContext.completeException(requestId,exception);
            }
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext application) {
        this.applicationContext = application;
    }

    private void processHeartBeatResponse(ChannelHandlerContext ctx, RpcResponse rpcResponse) {
        long sendTimestamp = rpcResponse.getTimeStamp();
        long rtt = System.currentTimeMillis() - sendTimestamp;
        applicationContext.getChannelProvider().recordRtt(ctx.channel(), rtt);
        if (log.isDebugEnabled()) {
            log.debug("接收到服务端{}的心跳检测响应,rtt为 {} ms", ctx.channel().remoteAddress(),
                    rtt);
        }
    }
}
