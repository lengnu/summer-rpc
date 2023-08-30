package com.duwei.summer.rpc.transport;

import com.duwei.summer.rpc.context.ApplicationContext;
import com.duwei.summer.rpc.exception.NetworkException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sun.reflect.generics.tree.Tree;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeMap;
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
@Data
public class ChannelProvider {
    public static final int CONNECTION_TIME = 3;
    public static final TimeUnit CONNECTION_UNIT = TimeUnit.SECONDS;

    public static final long DEFAULT_RTT = 100;

    private final ApplicationContext applicationContext;
    /**
     * 用于创建channel的锁
     */
    private final Object channelCreatLock = new Object();
    private final Map<InetSocketAddress, Channel> channelCache = new ConcurrentHashMap<>(16);
    private final Map<InetSocketAddress,Long> responseTimeCache = new ConcurrentHashMap<>(16);
//    private final NavigableSet<RttHolder> rttCache = new ConcurrentSkipListSet<>();
//    private final Map<Channel,RttHolder> channelRttHolderMap = new ConcurrentHashMap<>(16);
    private NettyClientStarter nettyClientStarter;


    public void recordRtt(InetSocketAddress channel, long time) {
        responseTimeCache.put(channel,time);
    }

    public long getRtt(InetSocketAddress inetSocketAddress) {
        Long rtt = responseTimeCache.get(inetSocketAddress);
        if (rtt == null) {
            return DEFAULT_RTT;
        }
        return rtt;
    }

    public ChannelProvider(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    public void removeChannel(InetSocketAddress address) {
        Channel channel = channelCache.get(address);
        if (channel != null) {
            channel.close();
            responseTimeCache.remove(address);
        }
        channelCache.remove(address);
        log.debug("与服务器{}断开连接，移除缓存移Channel", address);
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
                    channelCache.put(inetSocketAddress, channel);
                }
            }
        }
        return channel;
    }

    private Channel createChannel(InetSocketAddress inetSocketAddress) {
        if (nettyClientStarter == null) {
            nettyClientStarter = new NettyClientStarter(applicationContext);
        }

        Channel channel;
        CompletableFuture<Channel> channelCompleteFuture = new CompletableFuture<>();
        nettyClientStarter.connect(inetSocketAddress).addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RttHolder{
        private Channel channel;
        private InetSocketAddress inetSocketAddress;
        private long rtt;
    }

}
