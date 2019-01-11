package com.rpc.common.core;

import java.io.Serializable;

/**
 * @author craznail@gmail.com
 * @date 2018/12/27 14:53
 */
public class RpcResponse implements Serializable {
    private String requestId;
    private String error;
    private Object result;

    public String getRequestId() {
        return this.requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getResult() {
        return this.result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
