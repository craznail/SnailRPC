package nettty;

import com.rpc.common.core.RpcClientHandler;
import com.rpc.common.core.RpcRequest;
import com.rpc.common.core.RpcResponse;
import com.rpc.common.protocol.RpcDecoder;
import com.rpc.common.protocol.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

/**
 * @author craznail@gmail.com
 * @date 2019/1/10 9:59
 */
public class RpcClientDemo {

    private static RpcClientHandler andler = new RpcClientHandler();

    public static void main(String[] args) throws Exception {
        new RpcClientDemo().connect();
    }

    private void connect() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress("127.0.0.1", 8080))
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline ch = socketChannel.pipeline();
                            ch.addLast(new RpcEncoder(RpcRequest.class));
                            ch.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
                            ch.addLast(new RpcDecoder());
                            ch.addLast(new SimpleChannelInboundHandler<RpcResponse>() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    RpcRequest request = new RpcRequest();
                                    request.setRequestId("dsdssa");
                                    ctx.writeAndFlush(request);
                                }

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
                                    System.out.println("receive response:" + msg.getResult());
                                }
                            });
                        }
                    });
            ChannelFuture future = bootstrap.connect();
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) {
                    System.out.println("connect completed!");
                }
            });
            future.channel().closeFuture().sync();
        } catch (Exception ex) {
            group.shutdownGracefully();
        }
    }
}
