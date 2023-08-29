package com.duwei.summer.rpc.transport.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-29 15:22
 * @since: 1.0
 */
@Slf4j
public class RpcIdleStateHandler extends IdleStateHandler {
    private static final int READER_IDLE_TIME = 30;

    public RpcIdleStateHandler(){
        super(READER_IDLE_TIME,0,0);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        if (evt.state() == IdleState.READER_IDLE){
            log.debug("信道{}s内未读到数据,关闭连接",READER_IDLE_TIME);
            ctx.channel().close();
        }
    }
}
