package com.rpc.common.core;

import com.rpc.common.protocol.RpcDecoder;
import com.rpc.common.protocol.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;

/**
 * @author craznail@gmail.com
 * @date 2018/12/31 7:40
 */
public class NettyRpcClient implements IServiceRequest {
    private final static NettyRpcClient INSTANCE = new NettyRpcClient();
    private static RpcClientHandler rpcClientHandler = new RpcClientHandler();
    public final static NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
    private InetSocketAddress remoteAddress = new InetSocketAddress("127.0.0.1", 8080);
    private boolean isConnected = false;
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    //private Logger logger = LoggerFactory.getLogger(NettyRpcClient.class);

    private NettyRpcClient() {
    }

    public static NettyRpcClient getInstance() {
        return INSTANCE;
    }

    public void init() {
        connect();
    }

    @Override
    public CompletableFuture<?> send(RpcRequest request) {
        try {
            lock.lockInterruptibly();
            while (!isConnected) {
                condition.await();
            }
            return rpcClientHandler.send(request);
        } catch (InterruptedException ex) {
            //logger.error("send request interrupted", ex);
            return null;
        } finally {
            lock.unlock();
        }
    }

    private void connect() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline ch = socketChannel.pipeline();
                        ch.addLast(new RpcEncoder(RpcRequest.class));
                        ch.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0));
                        ch.addLast(new RpcDecoder());
                        ch.addLast(rpcClientHandler);
                    }
                });
        ChannelFuture future = bootstrap.connect(remoteAddress);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) {
                notifyConnected();
            }
        });
    }

    public void notifyConnected() {
        lock.lock();
        try {
            isConnected = true;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 停止
     */
    public void stop() {

    }
}
