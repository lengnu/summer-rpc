package com.duwei.summer.rpc.transport;

import com.duwei.summer.rpc.context.ApplicationContext;
import com.duwei.summer.rpc.context.ApplicationContextAware;
import com.duwei.summer.rpc.transport.codec.RequestDecoder;
import com.duwei.summer.rpc.transport.codec.ResponseEncoder;
import com.duwei.summer.rpc.transport.handler.RpcIdleStateHandler;
import com.duwei.summer.rpc.transport.handler.service.BaffleHandler;
import com.duwei.summer.rpc.transport.handler.service.MethodCallHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Netty服务端的启动器
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-27 14:56
 * @since: 1.0
 */
@Slf4j
public class NettyServerStarter {
    private final ApplicationContext applicationContext;

    public NettyServerStarter(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void start() {
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup work = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            List<ChannelHandler> channelHandlers = new ArrayList<>();
                            channelHandlers.add(new RpcIdleStateHandler());
                            channelHandlers.add(new LoggingHandler(LogLevel.DEBUG));
                            channelHandlers.add(new RequestDecoder());
                            channelHandlers.add(new BaffleHandler());
                            channelHandlers.add(new MethodCallHandler());
                            channelHandlers.add(new ResponseEncoder());
                            processAware(channelHandlers);
                            nioSocketChannel.pipeline().addLast(channelHandlers.toArray(new ChannelHandler[0]));
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(applicationContext.getPort()).sync();
            log.info("服务端在端口{}启动监听", applicationContext.getPort());
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                boss.shutdownGracefully().sync();
                work.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processAware(List<ChannelHandler> channelHandlers) {
        channelHandlers.stream()
                .filter(channelHandler -> channelHandler instanceof ApplicationContextAware)
                .forEach(channelHandler -> ((ApplicationContextAware) channelHandler).setApplicationContext(applicationContext));
    }

}
