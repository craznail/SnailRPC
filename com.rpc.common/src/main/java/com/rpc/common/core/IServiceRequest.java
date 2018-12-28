package com.rpc.common.core;

import java.util.concurrent.CompletableFuture;

/**
 * @author craznail@gmail.com
 * @date 2018/12/25 14:12
 */
public interface IServiceRequest {
    /**
     * 调用服务
     *
     * @param request
     * @return
     */
    CompletableFuture<?> send(RpcRequest request);
}
