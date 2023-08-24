package com.duwei.summer.rpc.transport.message.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 *  请求负载，描述需要调用接口的名字和参数列表
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 19:52
 * @since: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestPayload implements Serializable {
    /**
     * 接口名字(全限定名)
     */
    private String interfaceName;
    /**
     * 方法名字
     */
    private String methodName;
    /**
     * 参数类型
     */
    private Class<?>[] parametersType;
    /**
     * 具体参数
     */
    private Object[] parametersValue;
    /**
     * 返回值类型
     */
    private Class<?> returnType;

}
