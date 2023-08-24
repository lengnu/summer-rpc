package com.duwei.summer.rpc.transport.codec;

import com.duwei.summer.rpc.compress.CompressorFactory;
import com.duwei.summer.rpc.compress.CompressorWrapper;
import com.duwei.summer.rpc.serialize.SerializerFactory;
import com.duwei.summer.rpc.serialize.SerializerWrapper;
import com.duwei.summer.rpc.transport.message.request.RequestType;
import com.duwei.summer.rpc.transport.message.request.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


import static com.duwei.summer.rpc.transport.message.MessageFormatConstant.*;

/**
 * <p>
 * 4B magic            魔数
 * 1B version          版本号
 * 4B full length      报文总长度
 * 1B serialize        序列化类型
 * 1B compressed       压缩类型
 * 1B requestType      请求类型
 * 4B requestId        请求ID
 * 4B timestamp        时间戳
 * ~Boyd               不定长Body
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-22 20:00
 * @since: 1.0
 */
public class RequestEncoder extends MessageToByteEncoder<RpcRequest> {
    private static final byte[] EMPTY_BYTES = new byte[0];

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest, ByteBuf byteBuf) throws Exception {
        byte[] bodyBytes = getBodyBytes(rpcRequest);
        int fullLength = REQUEST_HEADER_TOTAL_LENGTH + bodyBytes.length;
        // 1.魔数
        byteBuf.writeInt(MAGIC);
        // 2.版本号
        byteBuf.writeByte(VERSION);
        // 3.报文长度
        byteBuf.writeInt(fullLength);
        // 4.序列化类型
        byteBuf.writeByte(rpcRequest.getSerializeType());
        // 5.压缩类型
        byteBuf.writeByte(rpcRequest.getCompressedType());
        // 6.报文类型
        byteBuf.writeByte(rpcRequest.getRequestType());
        // 7.请求ID
        byteBuf.writeLong(rpcRequest.getRequestId());
        // 8.写时间戳
        byteBuf.writeLong(rpcRequest.getTimeStamp());
        // 9.写Body
        if (bodyBytes != EMPTY_BYTES){
            byteBuf.writeBytes(bodyBytes);
        }
    }

    private byte[] getBodyBytes(RpcRequest rpcRequest) {
        if (rpcRequest.getRequestPayload() == null || rpcRequest.getRequestType() == RequestType.HEART_BEAT.getId()) {
            return EMPTY_BYTES;
        }
        // 1.序列化
        SerializerWrapper serializerWrapper = SerializerFactory.getSerializerWrapper(rpcRequest.getSerializeType());
        byte[] serializeBytes = serializerWrapper.getSerializer().serialize(rpcRequest.getRequestPayload());

        // 2.压缩
        CompressorWrapper compressorWrapper = CompressorFactory.getCompressorWrapper(rpcRequest.getCompressedType());
        return compressorWrapper.getCompressor().compress(serializeBytes);
    }
}
