package com.kian.intelligentbutler.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by YYTD on 2017/12/8.
 */

public class ThreadPool {

    private static ThreadPool instance;
    private final int EXECUTOR_THREADS = Runtime.getRuntime().availableProcessors();
    private ExecutorService processorsPools,cachedPools;

    private ThreadPool(){

    }

    public synchronized static ThreadPool getInstance(){
        if (instance==null){
            instance=new ThreadPool();
        }
        return instance;
    }

    public ExecutorService getProcessorsPools(){
        if (processorsPools==null){
            processorsPools = Executors.newFixedThreadPool(EXECUTOR_THREADS);
        }
        return processorsPools;
    }

    public ExecutorService getCachedPools(){
        if (cachedPools==null){
            cachedPools = Executors.newCachedThreadPool();
        }
        return cachedPools;
    }
}
