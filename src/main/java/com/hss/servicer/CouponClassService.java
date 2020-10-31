package com.hss.servicer;

import com.hss.bean.CouponClass;

public interface CouponClassService {

    /**
     * 由秘钥查找类目信息
     * 无锁
     * @param couponClass
     * @return
     */
    public CouponClass findCouponClass(CouponClass couponClass);

    /**
     * 由秘钥查找类目信息
     * 共享锁
     * @param couponClass
     * @return
     */
    public CouponClass findCouponClassDbShareLock(CouponClass couponClass);

    /**
     * 由秘钥查找类目信息
     * 共享锁
     * @param couponClass
     * @return
     */
    public CouponClass findCouponClassDbForUpdate(CouponClass couponClass);

    /**
     * 由编号查找
     * @return
     */
    public CouponClass findCouponClassById(Long id);

    public void updateCouponClassNumber(Long id,
                                        Integer number);
}
