package com.duwei.summer.rpc.compress;

import com.duwei.summer.rpc.exception.CompressException;
import com.duwei.summer.rpc.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;

import static com.duwei.summer.rpc.compress.CompressorFactory.COMPRESSOR_CACHE_BY_CLASS;

/**
 * <p>
 * 让工厂中注入一个压缩器
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-25 21:14
 * @since: 1.0
 */
@Slf4j
public class CompressorRegister {
    private static final AtomicInteger ID_NEXT = new AtomicInteger(1);


    public synchronized static void registerCompressIfNecessary(Class<? extends Compressor> compressorClass) {
        if (COMPRESSOR_CACHE_BY_CLASS.containsKey(compressorClass)) {
            registerCompressor(compressorClass);
        }
    }

    private synchronized static void registerCompressor(Class<? extends Compressor> compressorClass) {
        try {
            Compressor compressor = compressorClass.getConstructor().newInstance();
            byte id = (byte) ID_NEXT.getAndIncrement();
            String name = compressorClass.getSimpleName();
            CompressorFactory.registerCompressorInternal(id, name, compressor);
            log.debug("向容器中注入一个压缩器，类型{}", compressorClass);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            log.error("无法访问无参构造法");
            throw new CompressException("压缩器必须具有无参构造方法");
        }
    }
}
