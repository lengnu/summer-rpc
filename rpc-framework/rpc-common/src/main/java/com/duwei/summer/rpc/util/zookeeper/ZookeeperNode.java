package com.duwei.summer.rpc.util.zookeeper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *  Zookeeper简单封装类
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 16:22
 * @since: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZookeeperNode {
    private String path;
    private byte[] data;
}
