package serialize;

import com.rpc.common.core.RpcRequest;
import com.rpc.common.serializer.HessianUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author craznail@gmail.com
 * @date 2019/1/11 10:00
 */
public class HessianSerializeTest {
    @Test
    public void serializeTest(){
        RpcRequest request = new RpcRequest();
        request.setRequestId("testRequestId");
        try{
            byte[] resultBytes = HessianUtil.serialize(request);
            Object obj = HessianUtil.deserialize(resultBytes,null);
            RpcRequest requestReturn = (RpcRequest)obj;
            assertNotNull(requestReturn);
            assertEquals(requestReturn.getRequestId(), request.getRequestId());
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
