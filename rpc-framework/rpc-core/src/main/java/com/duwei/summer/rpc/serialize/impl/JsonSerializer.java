package com.duwei.summer.rpc.serialize.impl;

import com.alibaba.fastjson2.JSON;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.duwei.summer.rpc.exception.SerializeException;
import com.duwei.summer.rpc.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Objects;

/**
 * <p>
 * Json的序列化方式
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 21:41
 * @since: 1.0
 */
@Slf4j
public class JsonSerializer implements Serializer {
    @Override
    public byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return JSON.toJSONBytes(object);
        } catch (Exception e) {
            log.error("使用Json反序列化对象失败", e);
            throw new SerializeException(e);
        }
    }

    @Override
    public <T> T deserialize(Class<T> tClass, byte[] bytes) {
        Objects.requireNonNull(tClass);
        Objects.requireNonNull(bytes);
        try {
            return JSON.parseObject(bytes,tClass);
        } catch (Exception e) {
            log.error("使用Json反序列化对象失败", e);
            throw new SerializeException("使用Json反序列化对象失败");
        }
    }
}
