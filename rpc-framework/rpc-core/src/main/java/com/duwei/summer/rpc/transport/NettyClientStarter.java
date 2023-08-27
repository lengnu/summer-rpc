package com.duwei.summer.rpc.transport;

import com.duwei.summer.rpc.context.ApplicationContext;
import com.duwei.summer.rpc.context.ApplicationContextAware;
import com.duwei.summer.rpc.exception.NetworkException;
import com.duwei.summer.rpc.transport.codec.RequestEncoder;
import com.duwei.summer.rpc.transport.codec.ResponseDecoder;
import com.duwei.summer.rpc.transport.codec.ResponseEncoder;
import com.duwei.summer.rpc.transport.handler.client.RpcResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * <p>
 * 提供BootStrap单例
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 18:56
 * @since: 1.0
 */
@Slf4j
public class NettyClientStarter implements ApplicationContextAware {
    private Bootstrap bootstrap;
    private ApplicationContext applicationContext;

    public NettyClientStarter(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.init();
    }

    private void init() {
        bootstrap = new Bootstrap();
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        RpcResponseHandler rpcResponseHandler = new RpcResponseHandler();
                        rpcResponseHandler.setApplicationContext(applicationContext);
                        nioSocketChannel.pipeline()
                                .addLast(new LoggingHandler(LogLevel.DEBUG))
                                .addLast(new RequestEncoder())
                                .addLast(new ResponseDecoder())
                                .addLast(rpcResponseHandler);
                    }
                });
    }

    public ChannelFuture connect(InetSocketAddress inetSocketAddress) {
      return bootstrap.connect(inetSocketAddress);
    }

    @Override
    public void setApplicationContext(ApplicationContext application) {
        this.applicationContext = application;
    }
}
