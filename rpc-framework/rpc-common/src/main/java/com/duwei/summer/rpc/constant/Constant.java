package com.duwei.summer.rpc.constant;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-21 22:14
 * @since: 1.0
 */
public class Constant {
    /**
     * Zookeeper默认的连接地址
     */
    public static final String DEFAULT_ZK_CONNECTION = "127.0.0.1:2181";
    /**
     * Zookeeper默认连接超时时间
     */
    public static final int TIMEOUT = 10_000;

    /**
     * 服务提供方和调用放的在注册中心的基础路径
     */
    public static final String BASE_PROVIDERS_PATH = "/summer-rpc-metadata/providers";
    public static final String BASE_CONSUMERS_PATH = "/summer-rpc-metadata/consumers";
}
