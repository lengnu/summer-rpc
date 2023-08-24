package com.duwei.summer.rpc.compress.impl;

import com.duwei.summer.rpc.compress.Compressor;
import com.duwei.summer.rpc.exception.CompressException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * <p>
 * Gzip压缩器
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-23 09:53
 * @since: 1.0
 */
@Slf4j
public class GzipCompressor implements Compressor {

    private static final int BUFFER_SIZE = 1024;

    @Override
    public byte[] compress(byte[] bytes) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPOutputStream gos = new GZIPOutputStream(bos)) {
            gos.write(bytes);
            gos.finish();
            byte[] result = bos.toByteArray();
            if (log.isDebugEnabled()) {
                log.debug("使用Gzip压缩对象成功");
            }
            return result;
        } catch (IOException e) {
            log.error("使用Gzip压缩错误", e);
            throw new CompressException("使用Gzip压缩错误", e);
        }
    }

    @Override
    public byte[] decompress(byte[] bytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             GZIPInputStream gis = new GZIPInputStream(bis);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while (((read = gis.read(buffer)) > 0)) {
                bos.write(buffer, 0, read);
            }

            if (log.isDebugEnabled()) {
                log.debug("使用Gzip解压缩成功");
            }
            return bos.toByteArray();
        } catch (IOException e) {
            log.error("使用Gzip解压缩错误", e);
            throw new CompressException("使用Gzip解压缩错误", e);
        }
    }
}
