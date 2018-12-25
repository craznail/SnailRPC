package com.rpc.common.serializer;

import sun.reflect.misc.ReflectUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * @author craznail@gmail.com
 * @date 2018/12/24 13:01
 */
public class ObjectInputStreamWithLoader extends ObjectInputStream {
    private ClassLoader loader;

    public ObjectInputStreamWithLoader(InputStream in, ClassLoader theLoader)
            throws IOException {
        super(in);
        this.loader = theLoader;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass aClass)
            throws IOException, ClassNotFoundException {
        if (loader == null) {
            return super.resolveClass(aClass);
        } else {
            String name = aClass.getName();
            ReflectUtil.checkPackageAccess(name);
            // Query the class loader ...
            return Class.forName(name, false, loader);
        }
    }
}
