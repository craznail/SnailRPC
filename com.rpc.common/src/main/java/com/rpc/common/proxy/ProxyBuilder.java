package com.rpc.common.proxy;

import com.rpc.common.core.RpcClientHandler;
import com.rpc.common.core.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @author craznail@gmail.com
 * @date 2018/12/25 13:16
 */
public class ProxyBuilder {
    public static <T> T build(Class<T> clazz) {
        InvocationHandler handler = new RequestInvocationHandler();
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, handler);
    }

    static class RequestInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            RpcRequest request = new RpcRequest();
            request.setClassName(method.getDeclaringClass().getName());
            request.setMethodName(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);
            request.setRequestId(UUID.randomUUID().toString());

            return new RpcClientHandler().send(request);
        }
    }
}
