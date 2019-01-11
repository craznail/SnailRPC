package nettty;

import com.rpc.common.core.RpcRequest;
import com.rpc.common.core.RpcResponse;
import com.rpc.common.protocol.RpcDecoder;
import com.rpc.common.protocol.RpcEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

/**
 * @author craznail@gmail.com
 * @date 2019/1/9 17:13
 */
public class RpcServerDemo {
    public static void main(String[] args) throws Exception {
        int prot = 8080;
        if (args != null && args.length > 0) {
            try {
                prot = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // TODO: handle exception
            }
        }
        new RpcServerDemo().bind(prot);
    }

    public void bind(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress("127.0.0.1", 8080))
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new RpcEncoder(RpcResponse.class))
                                    .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0))
                                    .addLast(new RpcDecoder())
//                                    .addLast(new SimpleChannelInboundHandler<RpcRequest>() {
//                                        @Override
//                                        protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
//                                            System.out.println("get message:" + msg.getRequestId());
//                                            RpcResponse response = new RpcResponse();
//                                            response.setResult(5);
//                                            response.setRequestId(msg.getRequestId());
//                                            ctx.writeAndFlush(response);
//                                        }
//
                                    .addLast(new SimpleChannelInboundHandler<RpcRequest>(){
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
                                            System.out.println("receive request:" + request.getRequestId());
                                            RpcResponse response = new RpcResponse();
                                            response.setRequestId(request.getRequestId());
                                            response.setResult(5);
                                            ctx.write(response);
                                        }

                                        @Override
                                        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(
                                                    ChannelFutureListener.CLOSE
                                            );
                                        }
                                    });
                        }
                    });
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
