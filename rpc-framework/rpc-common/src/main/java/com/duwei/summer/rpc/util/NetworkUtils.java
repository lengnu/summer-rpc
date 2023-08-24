package com.duwei.summer.rpc.util;

import com.duwei.summer.rpc.exception.NetworkException;
import lombok.extern.slf4j.Slf4j;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * <p>
 *  网络接口工具类
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 16:56
 * @since: 1.0
 */
@Slf4j
public class NetworkUtils {
    /**
     * 获取网络IP
     *
     * @return 局域网IP
     */
    public static String getIp() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.isLoopback() ||
                        networkInterface.isVirtual() ||
                        !networkInterface.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof Inet6Address || inetAddress.isLoopbackAddress()) {
                        continue;
                    }
                    return inetAddress.getHostAddress();
                }
            }
        } catch (Exception e) {
            log.error("获取局域网IP发生异常", e);
            throw new NetworkException(e);
        }
        throw new NetworkException("未获取到局域网IP");
    }

}
