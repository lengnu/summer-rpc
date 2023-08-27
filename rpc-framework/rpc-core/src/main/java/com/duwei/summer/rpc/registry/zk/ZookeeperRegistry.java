package com.duwei.summer.rpc.registry.zk;

import com.duwei.summer.rpc.config.ServiceConfig;
import com.duwei.summer.rpc.constant.Constant;
import com.duwei.summer.rpc.exception.DiscoveryException;
import com.duwei.summer.rpc.exception.ZookeeperException;
import com.duwei.summer.rpc.registry.AbstractRegistry;
import com.duwei.summer.rpc.registry.RegistryConfig;
import com.duwei.summer.rpc.registry.zk.watch.PerceptionWatcher;
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
public class ZookeeperRegistry extends AbstractRegistry {
    private ZooKeeper zooKeeper;
    private static final int SESSION_TIMEOUT = 60 * 1000;

    public static final String BASE_ROOT_PATH = "/summer-rpc-metadata";
    public static final String BASE_ROOT_SERVICE_PATH = BASE_ROOT_PATH + "/services";


    public ZookeeperRegistry() {

    }

    private void createParentIfNeed(String path,CreateMode createMode) {
        if (!ZookeeperUtils.exists(zooKeeper, path, null)) {
            ZookeeperNode node = new ZookeeperNode(path, null);
            ZookeeperUtils.createNode(zooKeeper, node, createMode, null);
        }
    }

    @Override
    public void registry(ServiceConfig<?> serviceConfig) {
        // 服务名称节点，持久节点
        String parentNode = BASE_ROOT_SERVICE_PATH +
                "/" + serviceConfig.getInterfaceProvider().getName();
        createParentIfNeed(parentNode,CreateMode.PERSISTENT);

        // 分组节点
        String defaultNode = parentNode + "/" + serviceConfig.getGroup();
        createParentIfNeed(defaultNode,CreateMode.PERSISTENT);

        // 注册本机服务
        // 创建的是临时节点
        String nodePath = defaultNode + "/" + NetworkUtils.getIp() + ":" + getRegistryConfig().getApplicationContext().getPort();
        if (!ZookeeperUtils.exists(zooKeeper, nodePath, null)) {
            createParentIfNeed(nodePath,CreateMode.EPHEMERAL);
        }

        if (log.isDebugEnabled()) {
            log.debug("服务{}已经被注册,分组{}", serviceConfig.getInterfaceProvider(), serviceConfig
                    .getGroup());
        }
    }

    @Override
    public List<InetSocketAddress> lookup(String serviceName, String group) {
        String serviceNodePath = BASE_ROOT_SERVICE_PATH + "/" + serviceName + "/" + group;
        List<String> serviceNodeList = ZookeeperUtils.getChildren(
                zooKeeper,
                serviceNodePath,
                new PerceptionWatcher(getRegistryConfig().getApplicationContext()));
        if (serviceNodeList == null || serviceNodeList.size() == 0) {
            log.error("未找到可用的服务列表{}!", serviceName);
            throw new DiscoveryException("未找到可用的服务列表" + serviceName);
        }
        return serviceNodeList.stream().map(serviceNode -> {
            InetSocketAddress inetSocketAddress = parseInet(serviceNode);
            if (log.isDebugEnabled()) {
                log.debug("发现服务，名称{}，服务提供方地址{}", serviceName, inetSocketAddress);
            }
            return inetSocketAddress;
        }).collect(Collectors.toList());
    }

    @Override
    public void init() {
        RegistryConfig registryConfig = getRegistryConfig();
        String connectStr = registryConfig.getHost() + ":" + registryConfig.getHost();
        zooKeeper = ZookeeperUtils.createZookeeper(connectStr, SESSION_TIMEOUT);
        ZookeeperUtils.createNode(zooKeeper,
                new ZookeeperNode(BASE_ROOT_PATH, null),
                CreateMode.PERSISTENT,
                null
        );
        ZookeeperUtils.createNode(zooKeeper,
                new ZookeeperNode(BASE_ROOT_SERVICE_PATH, null),
                CreateMode.PERSISTENT,
                null
        );
    }

    private InetSocketAddress parseInet(String serviceNode) {
        try {
            String[] strings = serviceNode.split(":");
            String host = strings[0];
            int port = Integer.parseInt(strings[1]);
            return new InetSocketAddress(host, port);
        } catch (Exception e) {
            log.error("非法的节点路径，无法解析{}", serviceNode);
            throw new ZookeeperException("非法的节点路径，无法解析" + serviceNode);
        }
    }
}
