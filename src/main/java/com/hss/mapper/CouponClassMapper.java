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
    CouponClass findCouponClass(@Param(value = "couponClass") CouponClass couponClass,
                                       @Param(value = "tfLock") Boolean tfLock,
                                       @Param(value = "lockType") String lockType);

    /**
     * 由编号查找
     * @return
     */
    CouponClass findCouponClassById(@Param(value = "id") Long id);

    /**
     * 修改类目数量
     * @param id
     * @param number
     */
    void updateCouponClassNumber(@Param(value = "id") Long id,
                                        @Param(value = "number") Integer number);
}
