package com.duwei.summer.rpc.registry.zk;

import com.duwei.summer.rpc.compress.ServiceConfig;
import com.duwei.summer.rpc.constant.Constant;
import com.duwei.summer.rpc.exception.DiscoveryException;
import com.duwei.summer.rpc.registry.Registry;
import com.duwei.summer.rpc.util.NetworkUtils;
import com.duwei.summer.rpc.util.zookeeper.ZookeeperNode;
import com.duwei.summer.rpc.util.zookeeper.ZookeeperUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * Zookeeper实现的注册中心
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 17:10
 * @since: 1.0
 */
@Slf4j
public class ZookeeperRegistry implements Registry {
    private ZooKeeper zooKeeper;

    public ZookeeperRegistry(String connectionStr, int timeout) {
        this.zooKeeper = ZookeeperUtils.createZookeeper(connectionStr, timeout);
    }

    public ZookeeperRegistry() {
        this.zooKeeper = ZookeeperUtils.createZookeeper();
    }

    @Override
    public void registry(ServiceConfig<?> serviceConfig) {
        // 服务名称节点，持久节点
        String parentNode = Constant.BASE_PROVIDERS_PATH +
                "/" + serviceConfig.getInterfaceProvider().getName();

        if (!ZookeeperUtils.exists(zooKeeper, parentNode, null)) {
            ZookeeperNode node = new ZookeeperNode(parentNode, null);
            ZookeeperUtils.createNode(zooKeeper, node, CreateMode.PERSISTENT, null);
        }

        // TODO 后续处理端口
        String nodePath = parentNode + "/" + NetworkUtils.getIp() + ":" + 9999;

        if (log.isDebugEnabled()) {
            log.debug("服务{}已经被注册", serviceConfig);
        }
    }

    @Override
    public List<InetSocketAddress> lookup(String serviceName) {
        String serviceNodePath = Constant.BASE_PROVIDERS_PATH + "/" + serviceName;
        List<String> serviceNodeList = ZookeeperUtils.getChildren(zooKeeper, serviceNodePath, null);
        if (serviceNodeList == null || serviceNodeList.size() == 0) {
            throw new DiscoveryException("未找到可用的服务列表");
        }
        // TODO
        return serviceNodeList.stream().map(serviceNode -> {
            InetSocketAddress inetSocketAddress = parseInet(serviceNode);
            if (log.isDebugEnabled()) {
                log.debug("发现服务，名称{}，服务提供方地址{}", serviceName, inetSocketAddress);
            }
            return inetSocketAddress;
        }).collect(Collectors.toList());
    }

    /**
     * 从字符串解析出IP和Host
     */
    private InetSocketAddress parseInet(String serviceNode) {
        String[] ipAndPort = serviceNode.split(":");
        String ip = ipAndPort[0];
        int port = Integer.parseInt(ipAndPort[1]);
        return new InetSocketAddress(ip, port);
    }
}
