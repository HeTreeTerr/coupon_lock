package com.hss.servicer;

import com.hss.bean.CouponClass;

public interface CouponClassService {

    /**
     * 由秘钥查找类目信息
     * @param couponClass
     * @return
     */
    public CouponClass findCouponClass(CouponClass couponClass);

    /**
     * 由编号查找
     * @return
     */
    public CouponClass findCouponClassById(Long id);
}
