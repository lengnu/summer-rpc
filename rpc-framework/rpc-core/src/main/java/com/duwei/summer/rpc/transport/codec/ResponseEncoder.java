package com.duwei.summer.rpc.transport.codec;

import com.duwei.summer.rpc.compress.CompressorFactory;
import com.duwei.summer.rpc.compress.CompressorWrapper;
import com.duwei.summer.rpc.serialize.SerializerFactory;
import com.duwei.summer.rpc.serialize.SerializerWrapper;
import com.duwei.summer.rpc.transport.message.request.RequestType;
import com.duwei.summer.rpc.transport.message.request.RpcRequest;
import com.duwei.summer.rpc.transport.message.response.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import static com.duwei.summer.rpc.transport.message.MessageFormatConstant.*;

/**
 * <p>
 *  响应的编码器
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-23 11:30
 * @since: 1.0
 */
public class ResponseEncoder extends MessageToByteEncoder<RpcResponse> {
    private static final byte[] EMPTY_BYTES = new byte[0];

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse, ByteBuf byteBuf) throws Exception {
        byte[] bodyBytes = getBodyBytes(rpcResponse);
        int fullLength = RESPONSE_HEADER_TOTAL_LENGTH + bodyBytes.length;
        // 1.魔数
        byteBuf.writeInt(MAGIC);
        // 2.版本号
        byteBuf.writeByte(VERSION);
        // 3.报文长度
        byteBuf.writeInt(fullLength);
        // 4.状态码
        byteBuf.writeByte(rpcResponse.getCode());
        // 5.序列化类型
        byteBuf.writeByte(rpcResponse.getSerializeType());
        // 6.压缩类型
        byteBuf.writeByte(rpcResponse.getCompressedType());
        // 7.请求ID
        byteBuf.writeLong(rpcResponse.getRequestId());
        // 7.时间戳
        byteBuf.writeLong(rpcResponse.getTimeStamp());
        // 8.写Body
        if (bodyBytes != EMPTY_BYTES) {
            byteBuf.writeBytes(bodyBytes);
        }
    }

    private byte[] getBodyBytes(RpcResponse rpcResponse) {
        if (rpcResponse.getBody() == null) {
            return EMPTY_BYTES;
        }
        // 1.序列化
        SerializerWrapper serializerWrapper = SerializerFactory.getSerializerWrapper(rpcResponse.getSerializeType());
        byte[] serializeBytes = serializerWrapper.getSerializer().serialize(rpcResponse.getBody());

        // 2.压缩
        CompressorWrapper compressorWrapper = CompressorFactory.getCompressorWrapper(rpcResponse.getCompressedType());
        return compressorWrapper.getCompressor().compress(serializeBytes);
    }
}
