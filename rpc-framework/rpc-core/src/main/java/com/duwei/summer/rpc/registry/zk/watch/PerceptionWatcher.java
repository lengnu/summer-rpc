package com.duwei.summer.rpc.registry.zk.watch;

import com.duwei.summer.rpc.context.ApplicationContext;
import com.duwei.summer.rpc.context.ApplicationContextAware;
import com.duwei.summer.rpc.registry.Registry;
import com.duwei.summer.rpc.registry.RegistryConfig;
import com.duwei.summer.rpc.transport.ChannelProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 感知服务上下线
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-23 18:56
 * @since: 1.0
 */
@Slf4j
public class PerceptionWatcher implements Watcher {
    private final ApplicationContext applicationContext;

    public PerceptionWatcher(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
            log.debug("检测到服务{}有节点上下线，将重新拉取服务列表", watchedEvent.getPath());
            String serviceName = extractServiceName(watchedEvent.getPath());
            String group = extractServiceGroup(watchedEvent.getPath());
            // 新的服务列表
            List<InetSocketAddress> newServiceAddressList = applicationContext.getRegistryConfig().lookup(serviceName, group);
            // 老的服务列表
            List<InetSocketAddress> oldServiceAddress = applicationContext.getLoadBalancerConfig().getLoadBalancer().getServiceAddress(serviceName);
            ChannelProvider channelProvider = applicationContext.getChannelProvider();
            updateChannelWithService(newServiceAddressList, oldServiceAddress, channelProvider, serviceName);
            if (log.isDebugEnabled()) {
                log.debug("服务列表更新完成");
            }
        }
    }

    private void updateChannelWithService(List<InetSocketAddress> newServiceAddressList,
                                          List<InetSocketAddress> oldServiceAddress,
                                          ChannelProvider channelProvider,
                                          String serviceName) {
        List<InetSocketAddress> newAddInet;
        List<InetSocketAddress> removeInet;
        newAddInet = newServiceAddressList.stream().filter(
                inet -> !oldServiceAddress.contains(inet)
        ).collect(Collectors.toList());
        removeInet = oldServiceAddress.stream().filter(
                inet -> !newServiceAddressList.contains(inet)
        ).collect(Collectors.toList());
        // 更新服务列表
        applicationContext.getLoadBalancerConfig().getLoadBalancer().updateServiceList(serviceName, newServiceAddressList);
        // 删除所有老的连接
        removeInet.forEach(channelProvider::removeChannel);

    }

    private String extractServiceGroup(String path) {
        return path.substring(path.lastIndexOf('/') + 1);
    }

    private String extractServiceName(String path) {
        String[] split = path.split("/");
        return split[split.length - 2];
    }

}
