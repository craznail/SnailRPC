package com.rpc.common.proxy;

import com.rpc.common.core.NettyRpcClient;
import com.rpc.common.core.RpcRequest;
import com.rpc.common.core.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author craznail@gmail.com
 * @date 2018/12/25 13:16
 */
public class ProxyBuilder<B> {
    public <T> T build(Class<T> clazz) {
        InvocationHandler handler = new RequestInvocationHandler();
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, handler);
    }

    class RequestInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            RpcRequest request = new RpcRequest();
            request.setClassName(method.getDeclaringClass().getName());
            request.setMethodName(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);
            request.setRequestId(UUID.randomUUID().toString());

            try {
                var responseFuture = NettyRpcClient.getInstance().send(request);
                return (B) responseFuture.get().getResult();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }
}
