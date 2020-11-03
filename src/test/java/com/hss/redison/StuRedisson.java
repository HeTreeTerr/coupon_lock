package com.hss.redison;

import io.netty.util.concurrent.Future;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.RedissonClient;
import org.redisson.core.RLock;
import org.redisson.core.RReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StuRedisson {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 可重入锁(Reentrant Lock)
     */
    @Test
    public void lock(){

        RLock lock = redissonClient.getLock("stuRedisson:lock");
        /**
         * lock.lock();//不设置时效
         * lock.lock(10, TimeUnit.SECONDS);//设置时效
         * 1、如果指定了超时事件，就发送redis执行脚本，来进行占锁，
         * 默认超时时间就是我们指定的超时时间
         * 2.如果没有指定超时时间，就会使用30s[LockWatchTimeout看
         * 门狗默认事件]；只要占锁成功，就会启动一个定时任务[来重
         * 新给锁设定过期时间，新的过期时间就是看门狗默认的时间]，
         * 每个10秒就会自动再次续期，续成30s[LockWatchTimeout/3,就是10s]
         * 推荐使用：
         * lock.lock(10, TimeUnit.SECONDS);
         */
        //加锁
        lock.lock();//有看门狗机制
        /**
         * lock.tryLock();//尝试加锁
         * lock.tryLock(10,TimeUnit.SECONDS);//尝试加锁，加锁成功后，锁的有效时间
         * 机制和lock()一样。特点是有返回状态值，可以尝试并判断有没有加锁成功。并
         * 返回信息给用户
         */
        try{
            logger.info("todo my service...");
            Thread.sleep(5000);
        }catch (Exception e){
            new RuntimeException("阿巴阿巴阿巴...");
        }finally {
            //解锁
            lock.unlock();
        }
    }

    /**
     * 公平锁(Fair Lock)
     */
    @Test
    public void FairLock(){
        /**
         * 基于redission 可重入公平锁
         * 保证多个Redisson客户端线程同时请求加锁时，优先分配给先发请求的线程。所有请求
         * 的线程会在一个队列中排队。当一个线程宕机时，Redission会等待5秒后继续下一个线
         * 程，也就是说如果前面有5个线程都处于等待状态，那么后面的线程会等待至少25秒
         */
        //RLock fairLock = redissonClient.getFairLock("stuRedisson:lock");

        /**
         * lock.lock();//不设置时效
         * lock.lock(10, TimeUnit.SECONDS);//设置时效
         *
         * lock.tryLock();//尝试加锁
         * lock.tryLock(10,TimeUnit.SECONDS);//尝试加锁，加锁成功后，锁的有效时间
         *
         * lock.unlock();
         * 等方法与可重入锁类似
         */

        /**
         * 异步执行的相关方法
         */
        /*fairLock.lockAsync();
        fairLock.lockAsync(10,TimeUnit.SECONDS);

        fairLock.tryLockAsync();
        fairLock.tryLockAsync(10,TimeUnit.SECONDS);

        fairLock.unlockAsync();*/
    }

    /**
     * 读写锁
     */
    @Test
    public void writeReadLock(){
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("stuRedisson:writeReadLock");
        RLock rwLock = readWriteLock.readLock();
    }
}
