package com.hss.mapper;

import com.hss.bean.CouponClass;
import org.springframework.stereotype.Repository;

/**
 * 优惠券类目组件层
 */
@Repository
public interface CouponClassMapper {

    /**
     * 由秘钥查找类目信息
     * @param couponClass
     * @return
     */
    public CouponClass findCouponClass(CouponClass couponClass);
}
