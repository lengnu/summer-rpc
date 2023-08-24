package com.duwei.summer.rpc.registry;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

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
public class NettyBootstrapInitializer {
    private static Bootstrap bootstrap = new Bootstrap();

    static {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {

                    }
                });
    }

    public ChannelFuture connect(InetSocketAddress inetSocketAddress) {
        return bootstrap.connect(inetSocketAddress);
    }

    public NettyBootstrapInitializer() {

    }

    public static Bootstrap getBootstrap() {
        return bootstrap;
    }
}
