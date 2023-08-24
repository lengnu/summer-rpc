package com.duwei.summer.rpc.serialize.impl;

import com.duwei.summer.rpc.exception.SerializeException;
import com.duwei.summer.rpc.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Objects;

/**
 * <p>
 * JDK的序列化方式
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 21:41
 * @since: 1.0
 */
@Slf4j
public class JdkSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }
        try (
                ByteArrayOutputStream bas = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bas)
        ) {
            oos.writeObject(object);
            if (log.isDebugEnabled()) {
                log.debug("使用JDK方式序列化对象成功{}", object);
            }
            return bas.toByteArray();
        } catch (Exception e) {
            log.error("使用JDK反序列化对象失败", e);
            throw new SerializeException(e);
        }
    }

    @Override
    public <T> T deserialize(Class<T> tClass, byte[] bytes) {
        Objects.requireNonNull(tClass);
        Objects.requireNonNull(bytes);
        try (
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(bis)) {
            Object object = ois.readObject();
            if (log.isDebugEnabled()) {
                log.debug("使用JDK反序列化对象完成{}", object);
            }
            return tClass.cast(object);
        } catch (Exception e) {
            log.error("使用JDK反序列化对象失败", e);
            throw new SerializeException("使用JDK反序列化对象失败");
        }
    }
}
