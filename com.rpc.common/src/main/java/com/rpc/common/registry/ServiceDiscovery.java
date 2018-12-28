package com.rpc.common.registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author craznail@gmail.com
 * @date 2018/12/28 12:57
 */
public class ServiceDiscovery {
    private volatile List<String> dataList = new ArrayList<String>();

    public ServiceDiscovery() {
        dataList = Arrays.asList("PersonsService");
    }

    public String discover() {
        return dataList.get(0);
    }
}
