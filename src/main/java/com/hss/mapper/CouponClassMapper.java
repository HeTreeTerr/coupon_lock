package com.hss.mapper;

import com.hss.bean.CouponClass;
import org.apache.ibatis.annotations.Param;
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
    public CouponClass findCouponClass(@Param(value = "couponClass") CouponClass couponClass,
                                       @Param(value = "tfLock") Boolean tfLock,
                                       @Param(value = "lockType") String lockType);

    /**
     * 由编号查找
     * @return
     */
    public CouponClass findCouponClassById(@Param(value = "id") Long id);
}
