package com.rpc.common.protocol;

import com.rpc.common.serializer.HessianUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author craznail@gmail.com
 * @date 2019/1/1 7:30
 */
public class RpcEncoder extends MessageToByteEncoder {
    private Class<?> genericClass;

    public RpcEncoder(Class<?> clazz) {
        this.genericClass = clazz;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if (genericClass.isInstance(o)) {
            System.out.println(o.getClass().getSimpleName());
            byte[] data = HessianUtil.serialize(o);
            byteBuf.writeInt(data.length);
            byteBuf.writeBytes(data);
        }
    }
}
