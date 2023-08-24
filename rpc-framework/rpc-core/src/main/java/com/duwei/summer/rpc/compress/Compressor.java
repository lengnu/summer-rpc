package com.duwei.summer.rpc.compress;

/**
 * <p>
 *  压缩器接口
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-23 09:41
 * @since: 1.0
 */
public interface Compressor {
    /**
     * 对字节数组进行压缩
     * @param bytes 待压缩字节数组
     * @return  压缩后的字节数组
     */
    byte[] compress(byte[] bytes);
    /**
     * 对字节数组进行解压缩
     * @param bytes 待解压缩字节数组
     * @return  解压缩后的字节数组
     */
    byte[] decompress(byte[] bytes);
}
