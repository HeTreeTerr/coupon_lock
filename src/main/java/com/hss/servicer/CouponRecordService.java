package com.hss.servicer;

import com.hss.bean.CouponRecord;

public interface CouponRecordService {

    /**
     * 抢票核心逻辑(无锁)
     * @param userName
     * @param secretKey
     * @return
     */
    public CouponRecord grabCouponRecordNoneLock(String userName,String secretKey);

    /**
     * 抢票核心逻辑(数据库锁)
     * @param userName
     * @param secretKey
     * @return
     */
    public CouponRecord grabCouponRecordDbLock(String userName,String secretKey);
}
