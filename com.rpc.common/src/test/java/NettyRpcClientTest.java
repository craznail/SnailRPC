import com.rpc.common.core.NettyRpcClient;
import com.rpc.common.proxy.ProxyBuilder;

/**
 * @author craznail@gmail.com
 * @date 2019/1/6 19:57
 */
public class NettyRpcClientTest {
    public static void main(String args[]){
        NettyClientConnectTest();
    }
    public static void NettyClientConnectTest() {
        ICaculator caculator = ProxyBuilder.build(ICaculator.class);
        int sum = caculator.Sum(2, 3);
    }
}

interface ICaculator {
    int Sum(int a, int b);
}

class Caculator implements ICaculator {
    public int Sum(int a, int b) {
        return a + b;
    }
}
