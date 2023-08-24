package com.duwei.summer.rpc.serialize;

/**
 * <p>
 *  序列化器
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 21:38
 * @since: 1.0
 */
public interface Serializer {

    /**
     * 序列化对象
     * @param object    待序列化对象
     * @return  序列化后字节数组
     */
    byte[] serialize(Object object);

    /**
     * 反序列化
     * @param tClass    反序列化类型
     * @param bytes     反序列化字节序列
     * @param <T>   类型参数
     * @return  反序列化对象
     */
    <T> T deserialize(Class<T> tClass,byte[] bytes);
}
