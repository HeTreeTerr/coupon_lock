package com.hss.controller.handWrittenDistributedLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 手动实现分布式锁
 */
@RestController
@Controller
@RequestMapping(value = "/buy_goods")
public class DistributedLockDemoController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    private RedisTemplate redisTemplate;

    private final static String GOODKEY = "good:001";

    private final Lock lock = new ReentrantLock();

    /**
     * v1.0.0 单机版
     * 1.低并发下，没有问题
     * 2.高并发下，存在超卖超买问题
     *  原因：不能保证可见性、原子性、指令重排
     *  解决：加锁,两个都可以根据业务要求选型
     *  synchronized（jvm层面，自动释放锁，不可中断）
     *  lock（api层面，手动释放锁，可中断）
     * @return
     */
    @RequestMapping(value = "/v1.0.0")
    public String buyGoodsV1_0_0(){
//        1.查询库存
        Integer number = Integer.valueOf(redisTemplate.opsForValue().get(GOODKEY).toString());
//        2.库存充足
        if(number > 0)
        {
//            3.执行售货逻辑
            logger.info("服务{}====={}号商品，出售成功!",serverPort,number);
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            4.库存减一
            redisTemplate.opsForValue().set(GOODKEY,number-1);
            return number + "号商品，出售成功!";
        }
        return "商品已售完，库存不足";
    }

    /**
     * v2.0.0 单机版
     * 1.低并发先，没有问题
     * 2.高并发下，没有问题
     * 3.集群下，还是存在超卖超买问题
     * synchronized 不能中断，容易造成线程积压
     * @return
     */
    @RequestMapping(value = "/v2.0.0")
    public String buyGoodsV2_0_0(){
        synchronized (this){
//            1.查询库存
            Integer number = Integer.valueOf(redisTemplate.opsForValue().get(GOODKEY).toString());
//            2.库存充足
            if(number > 0)
            {
//                3.执行售货逻辑
                logger.info("服务{}====={}号商品，出售成功!",serverPort,number);
                try {
                    TimeUnit.MILLISECONDS.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                4.库存减一
                redisTemplate.opsForValue().set(GOODKEY,number-1);
                return number + "号商品，出售成功!";
            }
            return "商品已售完，库存不足";
        }
    }

    /**
     * v2.0.0 单机版
     * 1.低并发先，没有问题
     * 2.高并发下，没有问题
     * 3.集群下，还是存在超卖超买问题
     * tryLock 可以在短时间抢夺后，放弃
     * @return
     */
    @RequestMapping(value = "/v2.0.1")
    public String buyGoodsV2_0_1(){
        try {
//            获取锁
            if(lock.tryLock(3,TimeUnit.SECONDS)){
                try {
//                1.查询库存
                    Integer number = Integer.valueOf(redisTemplate.opsForValue().get(GOODKEY).toString());
//                2.库存充足
                    if(number > 0)
                    {
//                    3.执行售货逻辑
                        logger.info("服务{}====={}号商品，出售成功!",serverPort,number);
                        try {
                            TimeUnit.MILLISECONDS.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
//                    4.库存减一
                        redisTemplate.opsForValue().set(GOODKEY,number-1);
                        return number + "号商品，出售成功!";
                    }
                    return "商品已售完，库存不足";
                }finally {
//                    解锁
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "商品抢占失败，请稍后重试！";
    }
}
