package com.hss.servicer.impl;

import com.hss.bean.CouponRecord;
import com.hss.mapper.CouponRecordMapper;
import com.hss.servicer.DbAndCacheIdenticalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DbAndCacheIdenticalServiceImpl implements DbAndCacheIdenticalService {

    Logger logger = LoggerFactory.getLogger(getClass());

    private final static String KEY = "coupon:record";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CouponRecordMapper couponRecordMapper;

    @Override
    @Transactional
    public void saveDb(CouponRecord couponRecord) {
        logger.info("===========saveDb begin");
        couponRecordMapper.addCouponRecord(couponRecord);

        Boolean flag = false;
        if(flag){
            logger.error("===========saveDb fail");
            throw new RuntimeException("运行时异常！");
        }
        logger.info("===========saveDb success");
    }

    @Override
    @Transactional
    public void saveCache(CouponRecord couponRecord) {
        logger.info("===========saveCache begin");
        Long pushRes = redisTemplate.opsForList().leftPush(KEY,couponRecord);

        //redis 进入事务后，丧失了查询和返回能力
        logger.info("pushRes={}",pushRes);
        List recordList = redisTemplate.opsForList().range(KEY,-1,-1);
        logger.info("recordList={}",recordList);
        Object accountFrom = redisTemplate.opsForValue().get("user:account:from");
        logger.info("accountFrom={}",accountFrom);

        Boolean flag = false;
        if(flag){
            logger.error("===========saveCache fail");
            throw new RuntimeException("运行时异常！");
        }
        logger.info("===========saveCache success");
    }

    @Override
    @Transactional
    public void saveDbAndCache(CouponRecord couponRecord) {
        logger.info("===========saveDbAndCache begin");
        //写入数据库
        couponRecordMapper.addCouponRecord(couponRecord);
        //写入reids
        redisTemplate.opsForList().leftPush(KEY,couponRecord);
        Boolean flag = true;
        if(flag){
            logger.error("===========saveDbAndCache fail");
            throw new RuntimeException("运行时异常！");
        }
        logger.info("===========saveDbAndCache success");
    }
}
