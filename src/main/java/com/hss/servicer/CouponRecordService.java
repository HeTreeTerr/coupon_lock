package com.hss.servicer;

import com.hss.bean.CouponRecord;

public interface CouponRecordService {

    /**
     * 抢票核心逻辑(无锁)
     * 在高并发时，容易出现同一券出现多个得主
     * @param userName
     * @param secretKey
     * @return
     */
    public CouponRecord grabCouponRecordNoneLock(String userName,String secretKey);

    /**
     * 抢票核心逻辑(数据库锁)
     * 支持分布式多个jvm中的数据一致性，但使用不当容易造成死锁
     * @param userName
     * @param secretKey
     * @return
     */
    public CouponRecord grabCouponRecordDbLock(String userName,String secretKey);

    /**
     * 抢票核心逻辑(java锁)
     * 在单个jvm环境中可以保证数据一致性。但在分布式中，部署多态则无能为力
     * @param userName
     * @param secretKey
     * @return
     */
    public CouponRecord grabCouponRecordJavaLock(String userName,String secretKey);

    /**
     * 抢票核心逻辑(redis实现分布式锁)
     * 在单个jvm环境中可以保证数据一致性。但在分布式中，部署多态则无能为力
     * @param userName
     * @param secretKey
     * @return
     */
    public CouponRecord grabCouponRecordDistributedLock(String userName,String secretKey);
}
