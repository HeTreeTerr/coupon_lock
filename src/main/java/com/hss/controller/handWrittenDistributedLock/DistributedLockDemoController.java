package com.hss.controller.handWrittenDistributedLock;

import com.hss.util.RedissLockUtil;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
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

    private final static String REDISLOCK = "HSSLOCK";

    private final static String LOCK_LUA_SCRIPT =
            "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
            "then\n" +
            "return redis.call(\"del\",KEYS[1])\n" +
            "else\n" +
            "return 0\n" +
            "end";

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

    /**
     * v3.0.0 集群版
     * 手动使用redis作为锁
     * 压测观察，基本解决超买超卖问题
     *  遗留问题（极端情况下）：
     *  1.锁释放（服务抛出异常，没有解锁直接结束），造成其他线程永远无法拿到锁
     *  2.加锁后，服务或者redis宕机，导致锁一直在，造成其他线程永远无法拿到锁
     *  3.删除锁的时候，由于key一致，可能会误删锁，造成锁混乱
     * @return
     */
    @RequestMapping(value = "/v3.0.0")
    public String buyGoodsV3_0_0(){
        String value = UUID.randomUUID().toString() + Thread.currentThread().getName();
//        加锁
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(REDISLOCK, value);//setNX
        if(!flag){
//            抢锁失败
            return "商品抢占失败，请稍后重试！";
        }
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
//            解锁
            redisTemplate.delete(REDISLOCK);
            return number + "号商品，出售成功!";
        }
        return "商品已售完，库存不足";
    }

    /**
     * v4.0.0 集群版
     * 手动使用redis作为锁
     * 优化：finally中解锁，保证加锁必然会解锁
     * 压测观察，基本解决超买超卖问题
     *  遗留问题（极端情况下）：
     *  1.加锁后，服务或者redis宕机，导致锁一直在，造成其他线程永远无法拿到锁
     *  2.删除锁的时候，由于key一致，可能会误删锁，造成锁混乱
     * @return
     */
    @RequestMapping(value = "/v4.0.0")
    public String buyGoodsV4_0_0(){
        String value = UUID.randomUUID().toString() + Thread.currentThread().getName();
//        加锁
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(REDISLOCK, value);//setNX
        if(!flag){
//            枪锁失败
            return "商品抢占失败，请稍后重试！";
        }
        try {
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
        }finally {
//            解锁
            redisTemplate.delete(REDISLOCK);
        }
    }

    /**
     * v5.0.0 集群版
     * 手动使用redis作为锁
     * 优化：给reidslock加过期时间，预防服务或者redis宕机，导致锁一直在
     * 压测观察，基本解决超买超卖问题
     *  遗留问题（极端情况下）：
     *  1.上锁和赋予过期时间不是原子性
     *  2.线程阻塞，锁已经过期，但是线程还没有走完，又造成超卖超买
     *  3.删除锁的时候，由于key一致，可能会误删锁，造成锁混乱
     * @return
     */
    @RequestMapping(value = "/v5.0.0")
    public String buyGoodsV5_0_0(){
        String value = UUID.randomUUID().toString() + Thread.currentThread().getName();
//        加锁
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(REDISLOCK, value);//setNX
        if(flag){
//            设置过期时间
            redisTemplate.expire(REDISLOCK,1,TimeUnit.SECONDS);
        }
        if(!flag){
//            枪锁失败
            return "商品抢占失败，请稍后重试！";
        }
        try {
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
        }finally {
//            解锁
            redisTemplate.delete(REDISLOCK);
        }
    }

    /**
     * v6.0.0 集群版
     * 手动使用redis作为锁
     * 优化：保证加锁和赋予过期时间的原子性
     * 压测观察，基本解决超买超卖问题
     *  遗留问题（极端情况下）：
     *  1.线程阻塞，锁已经过期，但是线程还没有走完，又造成超卖超买
     *  2.删除锁的时候，由于key一致，可能会误删锁，造成锁混乱
     * @return
     */
    @RequestMapping(value = "/v6.0.0")
    public String buyGoodsV6_0_0(){
        String value = UUID.randomUUID().toString() + Thread.currentThread().getName();
//        加锁
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(REDISLOCK, value, 1, TimeUnit.SECONDS);//setNX
        if(!flag){
//            枪锁失败
            return "商品抢占失败，请稍后重试！";
        }
        try {
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
        }finally {
//            解锁
            redisTemplate.delete(REDISLOCK);
        }
    }

    /**
     * v7.0.0 集群版
     * 手动使用redis作为锁
     * 优化：finally块中，解锁前加校验
     * 压测观察，基本解决超买超卖问题
     *  遗留问题（极端情况下）：
     *  1.finally块中，判断和删除不是原子性
     *  2.线程阻塞，锁已经过期，但是线程还没有走完，又造成超卖超买
     * @return
     */
    @RequestMapping(value = "/v7.0.0")
    public String buyGoodsV7_0_0(){
        String value = UUID.randomUUID().toString() + Thread.currentThread().getName();
//        加锁
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(REDISLOCK, value, 1, TimeUnit.SECONDS);//setNX
        if(!flag){
//            枪锁失败
            return "商品抢占失败，请稍后重试！";
        }
        try {
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
        }finally {
            if(redisTemplate.opsForValue().get(REDISLOCK).equals(value)){
//                解锁
                redisTemplate.delete(REDISLOCK);
            }
        }
    }

    /**
     * v8.0.0 集群版
     * 手动使用redis作为锁
     * 优化：使用事务，保证finally块中，校验和删除不是原子性的
     * 压测观察，基本解决超买超卖问题
     *  遗留问题（极端情况下）：
     *  1.线程阻塞，锁已经过期，但是线程还没有走完，又造成超卖超买
     *  2.集群+cap（集群中，主节点没有同步数据到从节点就挂掉，造成数据丢失）
     * @return
     */
    @RequestMapping(value = "/v8.0.0")
    public String buyGoodsV8_0_0(){
        String value = UUID.randomUUID().toString() + Thread.currentThread().getName();
//        加锁
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(REDISLOCK, value, 10, TimeUnit.MINUTES);//setNX
        if(!flag){
//            枪锁失败
            return "商品抢占失败，请稍后重试！";
        }
        try {
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
        }finally {
            while (true){
                redisTemplate.watch(REDISLOCK);
                if(redisTemplate.opsForValue().get(REDISLOCK).equals(value)){
//                    解锁
                    redisTemplate.multi();
                    redisTemplate.delete(REDISLOCK);
                    List list = redisTemplate.exec();
                    if(list == null){
                        continue;
                    }
                }
                redisTemplate.unwatch();
                break;
            }
        }
    }


    /**
     * v8.0.1 集群版
     * 手动使用redis作为锁
     * 优化：使用lua脚本，保证finally块中，校验和删除不是原子性的
     * 压测观察，基本解决超买超卖问题
     *  遗留问题（极端情况下）：
     *  1.线程阻塞，锁已经过期，但是线程还没有走完，又造成超卖超买
     *  2.集群+cap（集群中，主节点没有同步数据到从节点就挂掉，造成数据丢失）
     * @return
     */
    @RequestMapping(value = "/v8.0.1")
    public String buyGoodsV8_0_1(){
        String value = UUID.randomUUID().toString() + Thread.currentThread().getName();
//        加锁
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(REDISLOCK, value, 10, TimeUnit.MINUTES);//setNX
        if(!flag){
//            枪锁失败
            return "商品抢占失败，请稍后重试！";
        }
        try {
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
        }finally {
            // 指定 lua 脚本，并且指定返回值类型
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(LOCK_LUA_SCRIPT,Long.class);
            Object result = redisTemplate.execute(redisScript, Collections.singletonList(REDISLOCK),value);
            if(result.toString().equals("1")){
                logger.info("解锁成功！");
            }else{
                logger.info("解锁失败！");
            }
        }
    }

    /**
     * v9.0.0 集群版
     * 使用redisson实现分布式锁
     * 地层：lua保证原子性 看门狗机制保证锁的有效期
     * 遗留问题（极端情况下）：
     * 集群+cap（集群中，主节点没有同步数据到从节点就挂掉，造成数据丢失）
     * @return
     */
    @RequestMapping(value = "/v9.0.0")
    public String buyGoodsV9_0_0(){
//        加锁
        RLock lock = RedissLockUtil.lock(REDISLOCK);
        try {
//            1.查询库存
            Integer number = Integer.valueOf(redisTemplate.opsForValue().get(GOODKEY).toString());
//            2.库存充足
            if(number > 0)
            {
//                3.执行售货逻辑
                logger.info("服务{}====={}号商品，出售成功!",serverPort,number);
//                4.库存减一
                redisTemplate.opsForValue().set(GOODKEY,number-1);
                return number + "号商品，出售成功!";
            }
            return "商品已售完，库存不足";
        }finally {
//            解锁
            if(lock.isLocked() && lock.isHeldByCurrentThread()){
                RedissLockUtil.unlock(REDISLOCK);
            }
        }
    }
}
