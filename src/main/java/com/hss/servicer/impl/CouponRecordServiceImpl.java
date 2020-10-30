package com.hss.servicer.impl;

import com.hss.bean.CouponClass;
import com.hss.bean.CouponRecord;
import com.hss.enums.LockEnum;
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
    public CouponRecord grabCouponRecordNoneLock(String userName, String secretKey) {
        CouponClass couponClass = new CouponClass();
        couponClass.setSecretKey(secretKey);

        //由密匙查找类目信息
        couponClass = couponClassService.findCouponClass(couponClass);
        if(null == couponClass || null == couponClass.getId() || null == couponClass.getNumber()){
            return null;
        }
        Integer allNumber = couponClass.getNumber();
        //统计已抢数量
        CouponRecord couponRecord = new CouponRecord();
        //赋予类目信息
        couponRecord.setCouponClass(couponClass);

        Integer nowNumber = couponRecordMapper.countCouponRecord(couponRecord,false,null);
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

    @Override
    public CouponRecord grabCouponRecordDbLock(String userName, String secretKey) {

        CouponClass couponClass = new CouponClass();
        couponClass.setSecretKey(secretKey);
        /**
         * (使用共享锁，其他事务仅可以获取相关行的共享锁。没有获取共享锁的事务不能修改或新增记录)
         * 在本方法中，考虑多线程，会有多个事务获取同一行的共享锁。
         * 但后面没有改表记录（增、删、改）的操作，共享锁足够
         */
        //由密匙查找类目信息
        couponClass = couponClassService.findCouponClassDbShareLock(couponClass);
        if(null == couponClass || null == couponClass.getId() || null == couponClass.getNumber()){
            return null;
        }
        Integer allNumber = couponClass.getNumber();
        //统计已抢数量
        CouponRecord couponRecord = new CouponRecord();
        //赋予类目信息
        couponRecord.setCouponClass(couponClass);
        //使用排他锁
        /**
         * （其他事务不能获得相关行的共享锁或排他锁，也不能新增记录）
         * 改方法，多线程环境下，或有多个事务申请锁，符合条件后再插入新的表记录
         * 所以共享锁不能符合条件（还是会出现多线程问题），故用排他锁。
         * 独占相关行的锁，本事务处理完后，其他事务才有机会抢占锁
         */
        Integer nowNumber = couponRecordMapper.countCouponRecord(couponRecord,true,LockEnum.FOT_UPDATE.toString());
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
