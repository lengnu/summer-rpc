package com.duwei.summer.rpc.transport.handler;

import com.duwei.summer.rpc.transport.codec.RequestEncoder;
import com.duwei.summer.rpc.transport.codec.ResponseDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * <p>
 *  消费端的初始化Handler
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 19:44
 * @since: 1.0
 */
public class ConsumerChannelInitializerHandler extends ChannelInitializer<NioSocketChannel>{
    @Override
    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
            nioSocketChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
            nioSocketChannel.pipeline().addLast(new RequestEncoder());
            nioSocketChannel.pipeline().addLast(new ResponseDecoder());
    }
}
