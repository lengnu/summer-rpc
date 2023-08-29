package com.duwei.summer.rpc.config;

import com.duwei.summer.rpc.annotation.RpcService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务配置
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-21 21:54
 * @since: 1.0
 */
@Data
@Builder
public class ServiceConfig<T> {
    private Class<?> interfaceProvider;
    private T ref;
    private String group = "default";

    public static List<ServiceConfig<?>> build(String className) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return build(Class.forName(className));
    }

    public static List<ServiceConfig<?>> build(Class<?> tClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Object instance = tClass.getConstructor().newInstance();
        return build(instance);
    }

    public static List<ServiceConfig<?>> build(Object bean) {
        Class<?> clazz = bean.getClass();
        RpcService annotation = clazz.getAnnotation(RpcService.class);
        String group = annotation.group();
        return Arrays.stream(clazz.getInterfaces()).map(interfaceName -> ServiceConfig.builder().group(group).interfaceProvider(interfaceName).ref(bean).build()).collect(Collectors.toList());
    }

}
