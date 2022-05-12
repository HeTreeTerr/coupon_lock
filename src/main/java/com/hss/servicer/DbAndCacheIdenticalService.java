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
     * 缓存 保存
     * redis
     * 单机支持事务（本质：乐观锁）
     * 集群不支持事务
     * @param couponRecord
     */
    void saveCache(CouponRecord couponRecord);
}
