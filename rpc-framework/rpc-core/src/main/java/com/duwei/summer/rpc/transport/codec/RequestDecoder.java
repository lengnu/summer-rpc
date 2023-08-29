package com.duwei.summer.rpc.transport.codec;

import com.duwei.summer.rpc.compress.CompressorFactory;
import com.duwei.summer.rpc.compress.CompressorWrapper;
import com.duwei.summer.rpc.exception.CodecException;
import com.duwei.summer.rpc.serialize.SerializerFactory;
import com.duwei.summer.rpc.transport.message.request.RequestPayload;
import com.duwei.summer.rpc.transport.message.request.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import static com.duwei.summer.rpc.transport.message.MessageFormatConstant.*;

/**
 * <p>
 * 消息解码器
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-23 10:47
 * @since: 1.0
 */
@Slf4j
public class RequestDecoder extends LengthFieldBasedFrameDecoder {
    public RequestDecoder() {
        super(MAX_FRAME_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH);

    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if (log.isDebugEnabled()){
            log.debug("接收到客户端{}请求，开始进行解码",ctx.channel().remoteAddress());
        }
        return decodeFrame(in);
    }


    private Object decodeFrame(ByteBuf byteBuf) {
        // 1.解析魔数
        int magic = byteBuf.readInt();
        if (magic != MAGIC) {
            log.error("魔数值不匹配,要求魔数{},消息魔数{}", MAGIC, magic);
            throw new CodecException("魔数值不匹配");
        }

        // 2.解析版本号
        byte version = byteBuf.readByte();
        if (version > VERSION) {
            log.error("版本不被支持");
            throw new CodecException("版本不被支持");
        }
        // 3.解析总长度
        int fullLength = byteBuf.readInt();
        // 4.解析序列化类型
        byte serializeType = byteBuf.readByte();
        // 5.解析压缩类型
        byte compressType = byteBuf.readByte();
        // 6.解析请求类型
        byte requestType = byteBuf.readByte();
        // 7.解析消息ID
        long requestId = byteBuf.readLong();
        // 8.读取时间戳
        long timestamp = byteBuf.readLong();
        // 9.解析响应体
        int bodyLength = fullLength - REQUEST_HEADER_TOTAL_LENGTH;
        byte[] body = new byte[bodyLength];
        byteBuf.readBytes(body);
        // 10.重构请求
        return RpcRequest.builder()
                .requestId(requestId)
                .requestType(requestType)
                .serializeType(serializeType)
                .compressedType(compressType)
                .timeStamp(timestamp)
                .requestPayload(parsePayload(serializeType,compressType,body))
                .build();
    }


    private RequestPayload parsePayload(byte serializeType,byte compressType,byte[] body){
        if (body != null && body.length != 0){
            CompressorWrapper compressorWrapper = CompressorFactory.getCompressorWrapper(compressType);
            byte[] decompress = compressorWrapper.getCompressor().decompress(body);
            return SerializerFactory.getSerializerWrapper(serializeType).getSerializer().deserialize(RequestPayload.class,decompress);
        }
        return null;
    }
}
