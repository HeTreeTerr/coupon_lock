package com.hss.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 看门狗基本使用
 */
public class WatchDogManagerTest {

    private static final Logger logger = LoggerFactory.getLogger(WatchDogManagerTest.class);

    public static void main(String[] args) throws InterruptedException {
        //启动看门狗线程
        WatchDogThreadManager.start();

        //启动五个执行耗时随机的线程
        for (int i = 0; i < 5; i++) {
            Runnable runnable = ()->{
                try {
                    TimeUnit.SECONDS.sleep(5 + genRandom());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    //线程结束，不监听
                    logger.info("{} is end",Thread.currentThread().getName());
                    WatchDogThreadManager.unWatch(Thread.currentThread());
                }
            };
            Thread thread = new Thread(runnable, "My-Thread-" + i);
            //监听
            WatchDogThreadManager.watch(thread);
            thread.start();
        }

        //主线程休眠两分钟，观察看门狗线程日志
        TimeUnit.MINUTES.sleep(2);
    }

    /**
     * 产生随机整数 0 <= a < VOLATILITY_EXPIRE_TIME
     * @return
     */
    private static Integer genRandom(){
        int randomNum = (int)(Math.random() * 20);
        return randomNum;
    }
}