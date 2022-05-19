package com.hss.servicer.impl;

import com.hss.servicer.CouponRecordService;
import com.hss.util.RedisUtil;
import com.hss.util.RedissLockUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.Redisson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CouponRecordServiceImplTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    public static StringRedisTemplate redisTemplate;

    @Autowired
    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        RedisUtil.redisTemplate = redisTemplate;
    }

    @Autowired
    private CouponRecordService couponRecordService;

    @Test
    public void grabCouponRecordNoneLock() {
    }

    @Test
    public void grabCouponRecordDbLock() {
    }

    @Test
    public void grabCouponRecordJavaLock() {
    }

    @Test
    public void grabCouponRecordDistributedLock() {
        couponRecordService.grabCouponRecordDistributedLock("","");
    }

    @Test
    public void redisLock(){
        //加锁
        RedissLockUtil.lock("com.hss.servicer.impl.CouponRecordServiceImplTest:redisLock");
        logger.info("jojojojojo");
        //解锁
        RedissLockUtil.unlock("com.hss.servicer.impl.CouponRecordServiceImplTest:redisLock");
    }
}