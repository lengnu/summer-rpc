package com.duwei.summer.rpc.compress;

import com.duwei.summer.rpc.compress.impl.GzipCompressor;
import com.duwei.summer.rpc.exception.SerializeException;
import com.duwei.summer.rpc.serialize.Serializer;
import com.duwei.summer.rpc.serialize.SerializerWrapper;
import com.duwei.summer.rpc.serialize.impl.HessianSerializer;
import com.duwei.summer.rpc.serialize.impl.JdkSerializer;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-23 09:42
 * @since: 1.0
 */
@Slf4j
public class CompressorFactory {
    private static final Map<String, CompressorWrapper> COMPRESSOR_CACHE_BY_NAME = new ConcurrentHashMap<>(4);
    private static final Map<Byte, CompressorWrapper> COMPRESSOR_CACHE_BY_TYPE = new ConcurrentHashMap<>(4);



    private static void registerCompressorInternal(byte type, String name, Compressor compressor) {
        if (COMPRESSOR_CACHE_BY_NAME.containsKey(name) || COMPRESSOR_CACHE_BY_TYPE.containsKey(type)) {
            throw new SerializeException("添加的序列化器name或type重复");
        }
        CompressorWrapper compressorWrapper = new CompressorWrapper(type, name, compressor);
        COMPRESSOR_CACHE_BY_NAME.put(name, compressorWrapper);
        COMPRESSOR_CACHE_BY_TYPE.put(type, compressorWrapper);
    }

    static {
        registerCompressorInternal(CompressorType.GZIP_CODE,CompressorType.GZIP,new GzipCompressor());
    }

    public static CompressorWrapper getCompressorWrapper(String serializerName) {
        CompressorWrapper wrapper = COMPRESSOR_CACHE_BY_NAME.get(serializerName);
        if (wrapper == null) {
            log.error("未找到合适的压缩器");
            throw new SerializeException("未找到合适的压缩器");
        }
        return wrapper;
    }

    public static CompressorWrapper getCompressorWrapper(byte serializerType) {
        CompressorWrapper wrapper = COMPRESSOR_CACHE_BY_TYPE.get(serializerType);
        if (wrapper == null) {
            log.error("未找到合适的解压缩器");
            throw new SerializeException("未找到合适的解压缩器");
        }
        return wrapper;
    }
}
