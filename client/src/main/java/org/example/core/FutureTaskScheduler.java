package org.example.core;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Classname FutureTaskScheduler
 * @Description TODO
 * @Date 2021/6/11 13:03
 * @Created by wangchao
 */
public class FutureTaskScheduler {
    private static final ThreadPoolExecutor threadPools = new ThreadPoolExecutor(10, 100, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    public static void submit(Runnable run) {
        threadPools.submit(run);
    }
}
