package com.hss.mapper;

import com.hss.bean.CouponClass;
import com.hss.bean.CouponRecord;
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
public class CouponRecordMapperTest {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CouponRecordMapper couponRecordMapper;

    @Test
    public void countCouponRecord() {
        //模拟类目信息
        CouponClass couponClass = new CouponClass();
        couponClass.setId(1L);
        //模拟记录信息
        CouponRecord couponRecord = new CouponRecord();
        couponRecord.setCouponClass(couponClass);

        Integer count = couponRecordMapper.countCouponRecord(couponRecord);
        logger.info("count-->"+count);
    }

    @Test
    public void findCouponRecord() {
    }

    @Test
    public void addCouponRecord() {
        //模拟类目信息
        CouponClass couponClass = new CouponClass();
        couponClass.setId(1L);
        //模拟记录信息
        CouponRecord couponRecord = new CouponRecord();
        couponRecord.setUserName("hss");
        couponRecord.setSeqNo(1);
        couponRecord.setCouponClass(couponClass);
        //执行新增
        couponRecordMapper.addCouponRecord(couponRecord);
        if(null != couponRecord.getId()){
            logger.info("新增成功 id==>"+couponRecord.getId());
        }else {
            logger.info("失败");
        }
    }

    @Test
    public void findCouponRecordById() {
        Long id = 1L;
        CouponRecord couponRecord = couponRecordMapper.findCouponRecordById(id);
        logger.info("couponRecord-->"+couponRecord);
    }
}