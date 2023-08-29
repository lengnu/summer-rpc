package com.duwei.summer.rpc.context.spi;

import com.duwei.summer.rpc.compress.Compressor;
import com.duwei.summer.rpc.compress.CompressorRegister;
import com.duwei.summer.rpc.loadbalance.LoadBalancer;
import com.duwei.summer.rpc.serialize.Serializer;
import com.duwei.summer.rpc.serialize.SerializerRegister;

/**
 * <p>
 *  从SPI加载序列化器和压缩器并注册到工厂中
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-29 18:40
 * @since: 1.0
 */
public class SpiReader {
    public static void findService(){
        // 1.进行序列化器的发现并注册
        SpiHandler<Serializer> serializers = SpiHandler.load(Serializer.class);
        for (Serializer serializer : serializers) {
            SerializerRegister.registerSerializerIfNecessary(serializer.getClass());
        }

        // 2.进行压缩器的发现并注册
        SpiHandler<Compressor> compressors = SpiHandler.load(Compressor.class);
        for (Compressor compressor : compressors) {
            CompressorRegister.registerCompressIfNecessary(compressor.getClass());
        }
    }
}
