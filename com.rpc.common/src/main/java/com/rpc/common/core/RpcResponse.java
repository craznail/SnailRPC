package com.rpc.common.core;

/**
 * @author craznail@gmail.com
 * @date 2018/12/27 14:53
 */
public class RpcResponse {
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

    private void setError(String error) {
        this.error = error;
    }

    private Object getResult() {
        return this.result;
    }

    private void setResult(Object result) {
        this.result = result;
    }
}
