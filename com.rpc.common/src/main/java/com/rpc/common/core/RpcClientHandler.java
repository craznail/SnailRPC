package com.rpc.common.core;

import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author craznail@gmail.com
 * @date 2018/12/27 14:43
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> implements IServiceRequest {
    private Logger logger = LoggerFactory.getLogger(RpcClientHandler.class);
    private Map<String, CompletableFuture<RpcResponse>> pendingRpc = new ConcurrentHashMap<>();
    private Channel channel;

    @Override
    public CompletableFuture send(RpcRequest request) {
        channel.writeAndFlush(request);
        CompletableFuture<RpcResponse> futrue = new CompletableFuture<>();
        pendingRpc.put(request.getRequestId(), futrue);
        return futrue;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        String requestId = rpcResponse.getRequestId();
        var rpcFuture = pendingRpc.get(requestId);
        if (rpcFuture != null) {
            pendingRpc.remove(requestId);
            rpcFuture.complete(rpcResponse);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("client caught exception", cause);
        ctx.close();
    }
}
