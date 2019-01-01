package com.rpc.common.core;

import com.rpc.common.protocol.RpcDecoder;
import com.rpc.common.protocol.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author craznail@gmail.com
 * @date 2018/12/31 7:40
 */
public class NettyRpcClient implements IServiceRequest {
    static {
        instance = new NettyRpcClient();
        handler = new RpcClientHandler();
    }

    private static NettyRpcClient instance = null;
    private static RpcClientHandler handler = null;

    public NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
    private InetSocketAddress remoteAddress = new InetSocketAddress("127.0.0.1", 5000);
    private boolean isConnected = false;
    private static ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    private NettyRpcClient() {
    }

    @Override
    public CompletableFuture<?> send(RpcRequest request) {
        return handler.send(request);
    }

    public void connect() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline ch = socketChannel.pipeline();
                        ch.addLast(new RpcEncoder(RpcRequest.class));
                        ch.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
                        ch.addLast(new RpcDecoder());
                        ch.addLast(new RpcClientHandler());
                    }
                });
        ChannelFuture future = bootstrap.connect(remoteAddress);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                isConnected = true;
                condition.signalAll();
            }
        });
    }

    public void waitForConnected() throws InterruptedException {
        lock.lockInterruptibly();
        while (!isConnected) {
            condition.await();
        }
        connect();
    }
}
