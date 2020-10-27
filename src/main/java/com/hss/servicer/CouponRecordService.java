package com.hss.servicer;

import com.hss.bean.CouponRecord;

public interface CouponRecordService {

    /**
     * 抢票核心逻辑
     * @param userName
     * @param secretKey
     * @return
     */
    public CouponRecord grabCouponRecord(String userName,String secretKey);
}
