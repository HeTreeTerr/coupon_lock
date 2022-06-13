package com.hss.servicer;

import com.hss.bean.CouponRecord;

/**
 * 数据库 和 缓存 一致性验证
 */
public interface DbAndCacheIdenticalService {

    /**
     * 数据库 保存
     * @param couponRecord
     */
    void saveDb(CouponRecord couponRecord);

    /**
     * 数据库 保存（测试 this 导致事务失效细节）
     * @param couponRecord
     */
    void saveTxDb(CouponRecord couponRecord);

    /**
     * 缓存 保存
     * redis
     * 单机支持事务（本质：乐观锁）
     * 集群不支持事务
     * @param couponRecord
     */
    void saveCache(CouponRecord couponRecord);

    /**
     * 数据库 缓存
     * 依靠事务，保证一致性
     * @param couponRecord
     */
    void saveDbAndCache(CouponRecord couponRecord);
}
