package com.hss.mapper;

import com.hss.bean.CouponClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CouponClassMapperTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CouponClassMapper couponClassMapper;

    @Test
    public void findCouponClass() {
        CouponClass couponClass = new CouponClass();
        couponClass.setSecretKey("HEQIZHENG");
        couponClass = couponClassMapper.findCouponClass(couponClass);
        logger.info("couponClass-->"+couponClass);
    }


    @Test
    public void findCouponClassById(){
        Long id = 1L;
        CouponClass couponClass = couponClassMapper.findCouponClassById(id);
        logger.info("couponClass-->"+couponClass);
    }
}