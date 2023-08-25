package com.duwei.summer.rpc.serialize;

import com.duwei.summer.rpc.exception.SerializeException;
import com.duwei.summer.rpc.serialize.impl.HessianSerializer;
import com.duwei.summer.rpc.serialize.impl.JsonSerializer;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 序列化器工厂
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 21:53
 * @since: 1.0
 */
@Slf4j
public class SerializerFactory {

    private static final Map<String, SerializerWrapper> SERIALIZER_CACHE_BY_NAME = new ConcurrentHashMap<>(4);
    private static final Map<Byte, SerializerWrapper> SERIALIZER_CACHE_BY_ID = new ConcurrentHashMap<>(4);
     static final Map<Class<? extends Serializer>,SerializerWrapper> SERIALIZER_CACHE_BY_CLASS = new LinkedHashMap<>(4);

    static synchronized void registerSerializerInternal(byte type, String name, Serializer serializer) {
        if (SERIALIZER_CACHE_BY_NAME.containsKey(name) || SERIALIZER_CACHE_BY_ID.containsKey(type) ||
        SERIALIZER_CACHE_BY_CLASS.containsKey(serializer.getClass())) {
            throw new SerializeException("添加的序列化器name、type或class重复");
        }
        SerializerWrapper serializerWrapper = new SerializerWrapper(type, name, serializer);
        SERIALIZER_CACHE_BY_NAME.put(name, serializerWrapper);
        SERIALIZER_CACHE_BY_ID.put(type, serializerWrapper);
        SERIALIZER_CACHE_BY_CLASS.put(serializer.getClass(), serializerWrapper);
    }

    static {
        registerSerializerInternal(SerializerType.JDK_CODE, SerializerType.JDK, new JsonSerializer());
        registerSerializerInternal(SerializerType.HESSIAN_CODE, SerializerType.HESSIAN, new HessianSerializer());
    }

    public static SerializerWrapper getSerializerWrapper(String serializerName) {
        SerializerWrapper wrapper = SERIALIZER_CACHE_BY_NAME.get(serializerName);
        if (wrapper == null) {
            log.error("未找到合适的序列化器");
            throw new SerializeException("未找到合适的序列化器");
        }
        return wrapper;
    }

    public static SerializerWrapper getSerializerWrapper(byte serializerType) {
        SerializerWrapper wrapper = SERIALIZER_CACHE_BY_ID.get(serializerType);
        if (wrapper == null) {
            log.error("未找到合适的序列化器");
            throw new SerializeException("未找到合适的序列化器");
        }
        return wrapper;
    }


    public static SerializerWrapper getSerializerWrapper(Class<? extends Serializer> serializerType) {
        SerializerWrapper wrapper = SERIALIZER_CACHE_BY_CLASS.get(serializerType);
        if (wrapper == null) {
            log.error("未找到合适的序列化器");
            throw new SerializeException("未找到合适的序列化器");
        }
        return wrapper;
    }

}