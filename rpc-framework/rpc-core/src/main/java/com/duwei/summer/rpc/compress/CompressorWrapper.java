package com.duwei.summer.rpc.compress;

import com.duwei.summer.rpc.serialize.Serializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *  压缩包装器
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 21:56
 * @since: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompressorWrapper {
    private byte type;
    private String name;
    private Compressor compressor;
}
