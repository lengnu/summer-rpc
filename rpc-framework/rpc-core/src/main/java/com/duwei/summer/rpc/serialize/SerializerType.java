package com.duwei.summer.rpc.serialize;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-23 09:49
 * @since: 1.0
 */
public interface SerializerType {
    /**
     * JDK序列化器的类型编码
     */
    byte JDK_CODE = 1;
    /**
     * Hessian序列化器的类型编码
     */
    byte HESSIAN_CODE = 2;
    /**
     * JDK序列化器名称
     */
    String JDK = "jdk";
    /**
     * Hessian序列化器名称
     */
    String HESSIAN = "hessian";
}
