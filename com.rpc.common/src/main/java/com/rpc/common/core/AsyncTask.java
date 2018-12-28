package com.rpc.common.core;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/**
 * @author craznail@gmail.com
 * @date 2018/12/28 13:57
 */
public abstract class AsyncTask<V> implements Runnable, Callable<V> {
    protected final Executor executor;

    public AsyncTask(Executor executor) {
        this.executor = executor;
    }


    public AsyncTask() {
        this(new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        });
    }

    @Override
    public void run() {
        Exception exp = null;
        V r = null;
        try {
            r = call();
        } catch (Exception e) {
            exp = e;
        }

        final V result = r;
        if (null == exp) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    onResult(result);
                }
            });
        } else {
            final Exception exceptionCaught = exp;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    onError(exceptionCaught);
                }
            });
        }
    }

    /**
     * override this method to implement task completion
     *
     * @param Result
     */
    protected abstract void onResult(V Result);

    protected void onError(Exception e) {
        e.printStackTrace();
    }
}
