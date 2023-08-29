package com.duwei.summer.rpc.context.spi;

import com.duwei.summer.rpc.loadbalance.LoadBalancer;
import com.duwei.summer.rpc.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import sun.swing.BakedArrayList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * <p>
 * SPI加载器
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-24 15:44
 * @since: 1.0
 */
@Slf4j
public class SpiHandler<T> implements Iterable<T>{
    private static final String BASE_PATH = "META-INF/services/";

    /**
     * 接口类
     */
    private final Class<T> service;

    /**
     * 实现类
     */
    private final List<T> providers;
    /**
     * 加载器
     */
    private final ClassLoader classLoader;
    /**
     * 该加载器是否进行过加载
     */
    private volatile boolean load;

    private static final Map<Class<?>, SpiHandler<?>> SPI_HANDLER_CACHE = new ConcurrentHashMap<>(4);

    public SpiHandler(Class<T> service) {
        this.service = service;
        this.providers = new ArrayList<>();
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    @SuppressWarnings("unchecked")
    public static <T> SpiHandler<T> load(Class<T> service) {
        SPI_HANDLER_CACHE.putIfAbsent(service, new SpiHandler<>(service));
        return (SpiHandler<T>) SPI_HANDLER_CACHE.get(service);
    }

    @SuppressWarnings("unchecked")
    private void doLoad() {
        try {
            Enumeration<URL> resources = classLoader.getResources(BASE_PATH + service.getName());
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                InputStream inputStream = url.openConnection().getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String implClassName = null;
                while ((implClassName = bufferedReader.readLine()) != null) {
                    Class<?> implClass = Class.forName(implClassName, false, classLoader);
                    if (service.isAssignableFrom(implClass)) {
                        Constructor<? extends T> constructor = (Constructor<? extends T>) implClass.getConstructor();
                        T instance = constructor.newInstance();
                        providers.add(instance);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Spi加载异常", e);
        }
    }

    public List<T> getProviders() {
        if (!load) {
            synchronized (this) {
                if (!load) {
                    doLoad();
                    load = true;
                }
            }
        }
        return providers;
    }

    @Override
    public Iterator<T> iterator() {
        return getProviders().iterator();
    }
}
