package com.rpc.common.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author craznail@gmail.com
 * @date 2018/12/24 14:02
 */
public class HessianUtil {
    public static byte[] serialize(Object obj) throws IOException {
        if (obj == null) {
            throw new NullPointerException();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HessianOutput ho = new HessianOutput(baos);
        ho.writeObject(obj);
        return baos.toByteArray();
    }

    public static Object deserialize(byte[] bytes, ClassLoader classLoader) throws IOException {
        if (bytes == null) {
            return null;
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ClassLoader old = null;
        if (classLoader != null) {
            old = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);
        }
        HessianInput hi = new HessianInput(bis);
        Object obj = hi.readObject();

        if (classLoader != null) {
            Thread.currentThread().setContextClassLoader(old);
        }
        return obj;
    }
}
