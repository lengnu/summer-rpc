package com.duwei.summer.rpc.spi;

import com.duwei.summer.rpc.loadbalance.LoadBalancer;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *  通过SPI加载配置项
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-23 17:10
 * @since: 1.0
 */
public class SpiHandler {
    private static final String PATH = "/META-INF/services";

    private static final Map<String, List<String>> SERVICE_NAME_CACHE = new ConcurrentHashMap<>(16);

    static {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            Enumeration<URL> resources = classLoader.getResources(PATH);
            while (resources.hasMoreElements()){
                URL url = resources.nextElement();
                System.out.println(url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    public static void main(String[] args) {

    }
}
