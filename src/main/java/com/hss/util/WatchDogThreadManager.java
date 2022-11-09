package com.hss.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 实现看门狗机制
 * 线程监控
 */
public class WatchDogThreadManager {

    private static final Logger logger = LoggerFactory.getLogger(WatchDogThreadManager.class);

    /**
     * 存放受监控的线程
     */
    private static ConcurrentHashMap<Long, Thread> map = new ConcurrentHashMap();

    /**
     * 监控线程
     * @param thread
     */
    public static void watch(Thread thread){
        map.put(thread.getId(),thread);
    }

    /**
     * 线程结束，解除监控
     * @param thread
     */
    public static void unWatch(Thread thread){
        map.remove(thread.getId());
    }

    /**
     * 启动一个线程，专门做线程监视
     */
    public static void start(){
        new Thread(()->{
                while (true){
                    Enumeration<Long> keys = map.keys();
                    while (keys.hasMoreElements()){
                        Long aLong = keys.nextElement();
                        Thread thread = map.get(aLong);
                        logger.info("thread:{}",thread);
                        if(thread != null){
                            logger.info("thread:{}监听",aLong);
                        }else{
                            map.remove(aLong);
                            logger.info("thread:{}不监听",aLong);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        }).start();
    }

}
