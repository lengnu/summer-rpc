package com.duwei.summer.rpc.transport.handler.client;

import com.duwei.summer.rpc.context.ApplicationContext;
import com.duwei.summer.rpc.context.ApplicationContextAware;
import com.duwei.summer.rpc.transport.message.request.RequestHelper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 客户端的心跳检测类
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-28 11:41
 * @since: 1.0
 */
@Slf4j
public class HeartBeatTimerHandler extends ChannelInboundHandlerAdapter {
    private static final long HEART_BEAT_INTERVAL = 10L;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        scheduleSendHeartBeat(ctx);
        super.channelActive(ctx);
    }

    public void scheduleSendHeartBeat(ChannelHandlerContext ctx) {
        ctx.executor().schedule(() -> {
            if (ctx.channel().isActive()) {
                if (log.isDebugEnabled()){
                    log.debug("发送心跳请求，服务端IP {}",ctx.channel().remoteAddress());
                }
                ctx.writeAndFlush(RequestHelper.buildHeartBeatRequest());
                scheduleSendHeartBeat(ctx);
            }
        }, HEART_BEAT_INTERVAL, TimeUnit.SECONDS);
    }
}
