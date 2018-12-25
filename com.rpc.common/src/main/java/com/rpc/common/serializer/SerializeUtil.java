package com.rpc.common.serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author craznail@gmail.com
 * @date 2018/12/24 10:38
 */
public class SerializeUtil {
    private static Logger log = LoggerFactory.getLogger(SerializeUtil.class);

    public static byte[] serialze(Object obj) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            return bos.toByteArray();
        } catch (Exception ex) {
            log.error("serialize error", ex);
        }
        return null;
    }

    public static <T> T deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object obj = ois.readObject();
            return (T) obj;
        } catch (Exception ex) {
            log.error("deserialize error", ex);
        }
        return null;
    }

    public static <T> T deserialze(byte[] bytes, ClassLoader classLoader) {
        if (bytes == null) {
            return null;
        }
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStreamWithLoader(bis, classLoader);
            return (T) ois.readObject();

        } catch (Exception ex) {
            log.error("deserilize error", ex);
        }
        return null;
    }
}
