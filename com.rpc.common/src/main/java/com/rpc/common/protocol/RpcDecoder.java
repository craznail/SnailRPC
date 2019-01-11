package com.rpc.common.protocol;

import com.rpc.common.serializer.HessianUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author craznail@gmail.com
 * @date 2019/1/1 7:55
 */
public class RpcDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < 4) {
            return;
        }
        int dataLength = byteBuf.readInt();
        byteBuf.markReaderIndex();
        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] bytes = new byte[dataLength];
        byteBuf.readBytes(bytes);
        Object a = HessianUtil.deserialize(bytes, this.getClass().getClassLoader());
        list.add(a);
    }
}
