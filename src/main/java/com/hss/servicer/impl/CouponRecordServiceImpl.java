package com.hss.servicer.impl;

import com.hss.bean.CouponClass;
import com.hss.bean.CouponRecord;
import com.hss.mapper.CouponRecordMapper;
import com.hss.servicer.CouponClassService;
import com.hss.servicer.CouponRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CouponRecordServiceImpl implements CouponRecordService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CouponRecordMapper couponRecordMapper;

    @Autowired
    private CouponClassService couponClassService;

    @Override
    public CouponRecord grabCouponRecord(String userName, String secretKey) {

        CouponClass couponClass = new CouponClass();
        couponClass.setSecretKey(secretKey);
        //由密匙查找类目信息(使用共享锁，其他事务仅可以获取相关行的共享锁。没有获取共享锁的事务不能修改或新增记录)
        couponClass = couponClassService.findCouponClass(couponClass);
        if(null == couponClass || null == couponClass.getId() || null == couponClass.getNumber()){
            return null;
        }
        Integer allNumber = couponClass.getNumber();
        //统计已抢数量
        CouponRecord couponRecord = new CouponRecord();
        //赋予类目信息
        couponRecord.setCouponClass(couponClass);
        //使用排他锁（其他事务不能获得相关行的共享锁或排他锁，也不能新增记录）
        Integer nowNumber = couponRecordMapper.countCouponRecord(couponRecord);
        if(nowNumber < allNumber){//以抢数小于总数
            try {//阻塞两秒，模拟业务运行耗时
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //赋予排序
            couponRecord.setSeqNo(nowNumber+1);
            //用户名
            couponRecord.setUserName(userName);
            couponRecordMapper.addCouponRecord(couponRecord);
            if(null != couponRecord.getId()){
                couponRecord = couponRecordMapper.findCouponRecordById(couponRecord.getId());
                return couponRecord;
            }
        }
        return null;
    }
}
