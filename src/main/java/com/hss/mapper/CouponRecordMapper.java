package com.hss.mapper;

import com.hss.bean.CouponRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 优惠记录组件层
 */
@Repository
public interface CouponRecordMapper {

    /**
     * 由类目编号查找以抢到的优惠券数量
     * @param couponRecord
     * @return
     */
    Integer countCouponRecord(@Param(value = "couponRecord") CouponRecord couponRecord,
                                     @Param(value = "tfLock") Boolean tfLock,
                                     @Param(value = "lockType") String lockType);

    /**
     * 由类目编号查找以抢到的优惠券记录
     * @param couponRecord
     * @return
     */
    List<CouponRecord> findCouponRecord(@Param(value = "couponRecord") CouponRecord couponRecord);

    /**
     * 新增抢券记录
     * @return
     */
    void addCouponRecord(CouponRecord couponRecord);

    /**
     * 查看抢券记录由编号
     * @return
     */
    CouponRecord findCouponRecordById(@Param(value = "id") Long id);
}
