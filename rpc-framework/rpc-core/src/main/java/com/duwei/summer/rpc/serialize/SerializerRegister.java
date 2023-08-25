package com.duwei.summer.rpc.serialize;

import com.duwei.summer.rpc.exception.SerializeException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;

import static com.duwei.summer.rpc.serialize.SerializerFactory.SERIALIZER_CACHE_BY_CLASS;

@Slf4j
public class SerializerRegister {
    private static final AtomicInteger ID_NEXT = new AtomicInteger(3);

    public static void registerSerializerIfNecessary(Class<? extends Serializer> serializerClass){
        if (SERIALIZER_CACHE_BY_CLASS.containsKey(serializerClass)){
            registerSerializer(serializerClass);
        }
    }

    private static void registerSerializer(Class<? extends Serializer> serializerClass) {
        try {
            Serializer serializer = serializerClass.getConstructor().newInstance();
            byte id = (byte) ID_NEXT.getAndIncrement();
            String name = serializerClass.getSimpleName();
            SerializerFactory.registerSerializerInternal(id, name, serializer);
            log.debug("向容器中注入一个序列化器，类型{}", serializerClass);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            log.error("序列化器必须具有无参构造方法");
            throw new SerializeException("序列化器必须具有无参构造方法");
        }
    }
}