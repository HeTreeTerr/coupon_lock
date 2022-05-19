package com.hss.redison;

import io.netty.util.concurrent.Future;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.*;
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
     * 读写锁(ReadWriteLock)
     * 保证一定能读到最新的数据，在修改期间，写锁是一个排他锁（互斥锁、独享锁），读锁是一个共享锁
     * 只要写锁存在没被释放，读操作就必须等待
     * 写+读：先写再读，必须等待写锁释放
     * 写+写：阻塞方式
     * 读+写：先读再写，有读锁，写也需要等待
     * 读+读：相当于无锁，并发读，只会在redis中记录好所有当前的读锁，他们都会同时加锁
     * 只要有写锁存在，读写都必须等待
     */
    @Test
    public void writeReadLock() throws InterruptedException {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("stuRedisson:writeReadLock");
        //获取读锁
        RLock rwLock = readWriteLock.readLock();
        //获取写锁
        //RLock rwLock = readWriteLock.writeLock();
        boolean tryLock = rwLock.tryLock(1,60,TimeUnit.SECONDS);
        if(tryLock){
            try {
                logger.info("to do my service..");
            }catch (Exception e){
                new RuntimeException();
            }finally {
                rwLock.unlock();
            }
        }else{
            System.out.println("获取锁失败");
        }
    }

    @Test
    public void writeReadLock1() throws InterruptedException {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("stuRedisson:writeReadLock");
        //获取读锁
        //RLock rwLock = readWriteLock.readLock();
        //获取写锁
        RLock rwLock = readWriteLock.writeLock();
        boolean tryLock = rwLock.tryLock(1,60,TimeUnit.SECONDS);
        if(tryLock){
            try {
                logger.info("to do my service..");
            }catch (Exception e){
                new RuntimeException();
            }finally {
                rwLock.unlock();
            }
        }else{
            System.out.println("获取锁失败");
        }
    }

    /**
     * 信号量Semaphore
     */
    @Test
    public void semaphoreLock(){
        RSemaphore semaphore = redissonClient.getSemaphore("park");
        /**
         * semaphore.acquire();
         * //或
         * semaphore.acquireAsync();
         * semaphore.acquire(23);
         * semaphore.tryAcquire();
         * //或
         * semaphore.tryAcquireAsync();
         * semaphore.tryAcquire(23, TimeUnit.SECONDS);
         * //或
         * semaphore.tryAcquireAsync(23, TimeUnit.SECONDS);
         * semaphore.release(10);
         * semaphore.release();
         * //或
         * semaphore.releaseAsync();
         *
         * acquire获取一个则信号量-1
         * release释放一个则信号量+1
         *
         * 信号量也可用于分布式限流
         */
        //+1(也可以自定义数值)
        semaphore.release();
        //-1(也可以自定义数值)
        boolean b = semaphore.tryAcquire();
        if(b){
            logger.info("to do my service..");
        }else{
            logger.info("获取锁失败");
        }
    }

    //闭锁（CountDownLatch）
    @Test
    public void countDownLatch(){
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.trySetCount(5);
        try {
            door.await();//等待闭锁都完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //执行其他业务
       logger.info("可以关大门了");
    }

    @Test
    public void countDownLatch1(){
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.countDown();//计数减一
        logger.info("班走完了");
    }

}
