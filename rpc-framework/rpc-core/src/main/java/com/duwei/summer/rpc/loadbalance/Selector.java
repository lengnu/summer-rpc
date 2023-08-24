package com.duwei.summer.rpc.loadbalance;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-23 15:29
 * @since: 1.0
 */
public interface Selector {
    InetSocketAddress next();
}
