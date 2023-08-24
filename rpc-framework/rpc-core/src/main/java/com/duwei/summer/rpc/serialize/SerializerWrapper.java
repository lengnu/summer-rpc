package com.duwei.summer.rpc.serialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *  序列化包装器
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 21:56
 * @since: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SerializerWrapper {
    private byte type;
    private String name;
    private Serializer serializer;
}
