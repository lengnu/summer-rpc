package com.duwei.rpc.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

/**
 * <p>
 *
 * <p>
 *
 * @author: duwei
 * @date: 2023-08-21 20:21
 * @since: 1.0
 */
public class NettyTest {
    public void testCompositeByteBuf(){
        ByteBuf head = Unpooled.buffer();
        ByteBuf body = Unpooled.buffer();
        CompositeByteBuf compositeBuffer = Unpooled.compositeBuffer();
        compositeBuffer.addComponent(head);
        compositeBuffer.addComponent(body);
    }
}
