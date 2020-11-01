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
        //获取剩余数量
        Integer number = couponClass.getNumber();

        if(number > 0){//剩余数量大于0
            /*try {//阻塞两秒，模拟业务运行耗时
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            CouponRecord couponRecord = new CouponRecord();
            couponRecord.setCouponClass(couponClass);
            //赋予排序
            couponRecord.setSeqNo(number);
            //用户名
            couponRecord.setUserName(userName);
            //添加抢券记录
            couponRecordMapper.addCouponRecord(couponRecord);
            //修改剩余量
            couponClassService.updateCouponClassNumber(couponClass.getId(),number-1);
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
         * 因为后面要对查出的类目进行修改。所以推荐使用排它锁
         */
        //由密匙查找类目信息
        couponClass = couponClassService.findCouponClassDbForUpdate(couponClass);
        if(null == couponClass || null == couponClass.getId() || null == couponClass.getNumber()){
            return null;
        }
        //获取剩余数量
        Integer number = couponClass.getNumber();

        if(number > 0){//剩余数量大于0
            /*try {//阻塞两秒，模拟业务运行耗时
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            CouponRecord couponRecord = new CouponRecord();
            couponRecord.setCouponClass(couponClass);
            //赋予排序
            couponRecord.setSeqNo(number);
            //用户名
            couponRecord.setUserName(userName);
            //添加抢券记录
            couponRecordMapper.addCouponRecord(couponRecord);
            //修改剩余量
            couponClassService.updateCouponClassNumber(couponClass.getId(),number-1);
            if(null != couponRecord.getId()){
                couponRecord = couponRecordMapper.findCouponRecordById(couponRecord.getId());
                return couponRecord;
            }
        }
        return null;
    }

    @Override
    public CouponRecord grabCouponRecordJavaLock(String userName, String secretKey) {
        CouponClass couponClass = new CouponClass();

        couponClass.setSecretKey(secretKey);
        //保证jvm中，一次只有一个线程持有该变量的锁
        //由密匙查找类目信息
        synchronized (secretKey){
            couponClass = couponClassService.findCouponClass(couponClass);
        }
        if(null == couponClass || null == couponClass.getId() || null == couponClass.getNumber()){
            return null;
        }
        //获取剩余数量
        Integer number = couponClass.getNumber();

        if(number > 0){//剩余数量大于0
            try {//阻塞两秒，模拟业务运行耗时
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            CouponRecord couponRecord = new CouponRecord();
            couponRecord.setCouponClass(couponClass);
            //赋予排序
            couponRecord.setSeqNo(number);
            //用户名
            couponRecord.setUserName(userName);
            //添加抢券记录
            couponRecordMapper.addCouponRecord(couponRecord);
            //修改剩余量
            couponClassService.updateCouponClassNumber(couponClass.getId(),number-1);
            if(null != couponRecord.getId()){
                couponRecord = couponRecordMapper.findCouponRecordById(couponRecord.getId());
                return couponRecord;
            }
        }
        return null;
    }


}
