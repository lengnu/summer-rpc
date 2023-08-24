package com.duwei.summer.rpc.util.zookeeper;

import com.duwei.summer.rpc.constant.Constant;
import com.duwei.summer.rpc.exception.ZookeeperConnectionException;
import com.duwei.summer.rpc.exception.ZookeeperException;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

/**
 * <p>
 * Zookeeper工具类
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 15:16
 * @since: 1.0
 */
@Slf4j
public class ZookeeperUtils {

    /**
     * 采用默认配置创建Zookeeper客户端
     *
     * @return Zookeeper客户端
     */
    public static ZooKeeper createZookeeper() {
        return createZookeeper(Constant.DEFAULT_ZK_CONNECTION, Constant.TIMEOUT);
    }

    public static ZooKeeper createZookeeper(String connectionStr, int sessionTimeout) {
        ZooKeeper zooKeeper = null;
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String connectString = Constant.DEFAULT_ZK_CONNECTION;
        try {
            zooKeeper = new ZooKeeper(connectString, sessionTimeout, watchedEvent -> {
                if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    log.info("Zookeeper连接成功，地址{}", connectString);
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
        } catch (IOException | InterruptedException exception) {
            log.error("Zookeeper连接失败，地址{}", connectString);
            throw new ZookeeperConnectionException();
        }
        return zooKeeper;
    }

    /**
     * 创建节点
     *
     * @param zooKeeper  客户端
     * @param node       创建节点
     * @param createMode 节点类型
     * @param watcher    监听
     * @return true: 创建成功 false: 已经存在 Exception: 出现异常
     */
    public static boolean createNode(ZooKeeper zooKeeper,
                                     ZookeeperNode node,
                                     CreateMode createMode,
                                     Watcher watcher) {
        Objects.requireNonNull(zooKeeper);
        Objects.requireNonNull(node);
        try {
            if (zooKeeper.exists(node.getPath(), watcher) == null) {
                zooKeeper.create(node.getPath(),
                        node.getData(),
                        null,
                        createMode
                );
                log.info("节点{}创建成功", node.getPath());
                return true;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("节点{}已经存在", node.getPath());
                }
            }
        } catch (InterruptedException | KeeperException e) {
            log.error("节点{}创建失败", node.getPath(), e);
            throw new ZookeeperException();
        }
        return false;
    }

    public static void close(ZooKeeper zooKeeper) {
        if (zooKeeper != null) {
            try {
                zooKeeper.close();
                log.info("Zookeeper关闭成功");
            } catch (InterruptedException e) {
                log.error("关闭Zookeeper发生异常", e);
                throw new ZookeeperException();
            }
        }
    }

    /**
     * 判断节点是否存在
     */
    public static boolean exists(ZooKeeper zooKeeper, String path, Watcher watcher) {
        try {
            return zooKeeper.exists(path, watcher) != null;
        } catch (KeeperException | InterruptedException e) {
            throw new ZookeeperException(e);
        }
    }


    /**
     * 获取指定路径下的节点
     *
     * @param zooKeeper Zk客户端
     * @param nodePath  节点路径
     * @return 子节点列表
     */
    public static List<String> getChildren(ZooKeeper zooKeeper, String nodePath, Watcher watcher) {
        try {
            return zooKeeper.getChildren(nodePath, watcher);
        } catch (KeeperException | InterruptedException e) {
            log.error("获取节点子元素列表失败，父节点路径{}", nodePath);
            throw new ZookeeperException(e);
        }
    }
}
