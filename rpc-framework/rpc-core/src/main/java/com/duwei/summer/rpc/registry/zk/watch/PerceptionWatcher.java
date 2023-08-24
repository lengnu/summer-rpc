package com.duwei.summer.rpc.registry.zk.watch;

import com.duwei.summer.rpc.registry.Registry;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * <p>
 *  感知服务上下线
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-23 18:56
 * @since: 1.0
 */
@Slf4j
public class PerceptionWatcher implements Watcher {
    private Registry registry;

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType() == Event.EventType.NodeDataChanged){
                log.debug("检测到服务{}有节点上下线，将重新拉取服务列表",watchedEvent.getPath());
//                registry.lookup()
        }
    }

    private String extractServiceName(String  path){
        return path.substring(path.lastIndexOf('/'));
    }
}
