package com.duwei.summer.rpc.compress;

/**
 * <p>
 *  压缩器类型常量
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-23 09:49
 * @since: 1.0
 */
public interface CompressorType {
    /**
     * GZIP压缩器编码
     */
    byte GZIP_CODE = 1;
    /**
     * GZIP压缩器名称
     */
    String GZIP = "gzip";
}
