package nettty;

import com.rpc.common.core.NettyRpcClient;
import com.rpc.common.core.RpcRequest;
import com.rpc.common.protocol.RpcDecoder;
import com.rpc.common.protocol.RpcEncoder;
import com.rpc.common.proxy.ProxyBuilder;
import com.rpc.common.serializer.HessianUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;

/**
 * @author craznail@gmail.com
 * @date 2019/1/6 19:57
 */
public class NettyRpcClientTest {

    @Test
    public void NettyClientConnectTest() throws Exception {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                NettyRpcClient.getInstance().init();
            }
        });
        thread.start();

        ICaculator caculator = new ProxyBuilder<Integer>().build(ICaculator.class);
        CompletableFuture<Integer> sumFuture = CompletableFuture.supplyAsync(() -> {
            return caculator.Sum(2, 3);
        });
        sumFuture.whenComplete((e, v) -> {
            assertEquals(e.intValue(), 5);
        });
        assertEquals(sumFuture.get().intValue(), 5);
    }

    @Test
    public void RpcEncoderTest() {
        RpcRequest request = new RpcRequest();
        request.setRequestId("testRequestId");
        EmbeddedChannel channel = new EmbeddedChannel(
                new RpcEncoder(RpcRequest.class)
        );
        assertTrue(channel.writeOutbound(request));
        assertTrue(channel.finish());

        //read Bytes
        ByteBuf read = (ByteBuf) channel.readOutbound();
        byte[] bytes = new byte[read.readableBytes() - 4];
        var length = read.readInt();
        read.readBytes(bytes);

        try {
            Object result = HessianUtil.deserialize(bytes, null);
            RpcRequest requestReturn = (RpcRequest) result;
            assertNotNull(requestReturn);
            assertEquals(requestReturn.getRequestId(), request.getRequestId());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void RpcDecoderTest() throws Exception {
        RpcRequest request = new RpcRequest();
        request.setRequestId("testRequestId");
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(65535, 0, 4, 0, 0),
                new RpcDecoder()
        );

        byte[] data = HessianUtil.serialize(request);
        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(data.length);
        buf.writeBytes(data);


        assertTrue(channel.writeInbound(buf));
        var inboudData = channel.readInbound();
        var requestServer = (RpcRequest) inboudData;
        assertEquals(requestServer.getRequestId(), request.getRequestId());
    }

    @Test
    public void RpcEncoderDecoderTest() throws Exception {
        RpcRequest request = new RpcRequest();
        request.setRequestId("testRequestId");
        EmbeddedChannel channel = new EmbeddedChannel(
                new RpcEncoder(RpcRequest.class),
                new LengthFieldBasedFrameDecoder(65535, 0, 4, 0, 0),
                new RpcDecoder()
        );
        channel.writeOutbound(request);
        ByteBuf encodedOut = channel.readOutbound();
        channel.writeInbound(encodedOut);
        RpcRequest requstServer = (RpcRequest) channel.readInbound();
        assertEquals(requstServer.getRequestId(), request.getRequestId());
    }
}