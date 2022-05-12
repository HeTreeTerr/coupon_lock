package com.hss.servicer;

import com.hss.bean.CouponClass;

public interface CouponClassService {

    /**
     * 由秘钥查找类目信息
     * 无锁
     * @param couponClass
     * @return
     */
    CouponClass findCouponClass(CouponClass couponClass);

    /**
     * 由秘钥查找类目信息
     * 共享锁
     * @param couponClass
     * @return
     */
    CouponClass findCouponClassDbShareLock(CouponClass couponClass);

    /**
     * 由秘钥查找类目信息
     * 共享锁
     * @param couponClass
     * @return
     */
    CouponClass findCouponClassDbForUpdate(CouponClass couponClass);

    /**
     * 由编号查找
     * @return
     */
    CouponClass findCouponClassById(Long id);

    /**
     * 更新类目数量
     * @param id
     * @param number
     */
    void updateCouponClassNumber(Long id, Integer number);
}
