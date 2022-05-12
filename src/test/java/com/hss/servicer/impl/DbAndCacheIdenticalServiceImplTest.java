package com.hss.servicer.impl;

import com.hss.bean.CouponClass;
import com.hss.bean.CouponRecord;
import com.hss.mapper.CouponClassMapper;
import com.hss.servicer.DbAndCacheIdenticalService;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ObjectUtils;

import java.util.Random;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DbAndCacheIdenticalServiceImplTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DbAndCacheIdenticalService dbAndCacheIdenticalService;

    @Autowired
    private CouponClassMapper couponClassMapper;

    @Test
    public void saveDb() {

        CouponClass couponClass = couponClassMapper.findCouponClassById(1L);
        if(!ObjectUtils.isEmpty(couponClass)){

            CouponRecord record = new CouponRecord();
            record.setCouponClass(couponClass);
            record.setUserName("hss");
            record.setSeqNo(new Random().nextInt(10));
            dbAndCacheIdenticalService.saveDb(record);
        }else {
            logger.info("======类目信息为空");
        }
    }

    @Test
    public void saveCache() {
        CouponClass couponClass = couponClassMapper.findCouponClassById(1L);
        if(!ObjectUtils.isEmpty(couponClass)){

            CouponRecord record = new CouponRecord();
            record.setCouponClass(couponClass);
            record.setUserName("hss");
            record.setSeqNo(new Random().nextInt(10));
            dbAndCacheIdenticalService.saveCache(record);
        }else {
            logger.info("======类目信息为空");
        }
    }

    @Test
    public void saveDbAndCache(){
        CouponClass couponClass = couponClassMapper.findCouponClassById(1L);
        if(!ObjectUtils.isEmpty(couponClass)){

            CouponRecord record = new CouponRecord();
            record.setCouponClass(couponClass);
            record.setUserName("hss");
            record.setSeqNo(new Random().nextInt(10));
            dbAndCacheIdenticalService.saveDbAndCache(record);
        }else {
            logger.info("======类目信息为空");
        }
    }
}