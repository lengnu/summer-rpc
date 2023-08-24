package com.duwei.summer.rpc.transport;

import com.duwei.summer.rpc.exception.NetworkException;
import com.duwei.summer.rpc.registry.NettyBootstrapInitializer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.*;

/**
 * <p>
 * 维护和管理channel
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-23 17:14
 * @since: 1.0
 */
@Slf4j
public class ChannelProvider {
    public static final int CONNECTION_TIME = 3;
    public static final TimeUnit CONNECTION_UNIT = TimeUnit.SECONDS;
    /**
     * 用于创建channel的锁
     */
    private final Object channelCreatLock = new Object();
    private final NettyBootstrapInitializer nettyBootstrapInitializer = new NettyBootstrapInitializer();
    private final Map<InetSocketAddress, Channel> channelCache = new ConcurrentHashMap<>(16);

    public void addChannel(InetSocketAddress address, Channel channel) {
        channelCache.put(address, channel);
    }

    public void removeChannel(InetSocketAddress address) {
        channelCache.remove(address);
    }


    /**
     * 获取一个可用的通道
     *
     * @return 与服务器建立的通道
     */
    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelCache.get(inetSocketAddress);
        if (channel == null) {
            synchronized (channelCreatLock) {
                channel = channelCache.get(inetSocketAddress);
                if (channel == null) {
                    channel = createChannel(inetSocketAddress);
                    channelCache.put(inetSocketAddress,channel);
                }
            }
        }
        return channel;
    }

    private Channel createChannel(InetSocketAddress inetSocketAddress) {
        Channel channel;
        CompletableFuture<Channel> channelCompleteFuture = new CompletableFuture<>();
        ChannelFuture future = nettyBootstrapInitializer.connect(inetSocketAddress).addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isDone()) {
                if (log.isDebugEnabled()) {
                    log.debug("与服务器{}成功建立了连接", inetSocketAddress);
                }
                channelCompleteFuture.complete(channelFuture.channel());
            } else if (!channelFuture.isSuccess()) {
                channelCompleteFuture.completeExceptionally(channelFuture.cause());
            }
        });

        try {
            channel = channelCompleteFuture.get(CONNECTION_TIME, CONNECTION_UNIT);
        } catch (ExecutionException | TimeoutException | InterruptedException e) {
            log.error("获取通道发生异常");
            throw new NetworkException("连接服务器发生异常");
        }
        return channel;
    }

}
