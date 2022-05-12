package com.hss.servicer.impl;

import com.hss.bean.CouponRecord;
import com.hss.mapper.CouponRecordMapper;
import com.hss.servicer.DbAndCacheIdenticalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DbAndCacheIdenticalServiceImpl implements DbAndCacheIdenticalService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CouponRecordMapper couponRecordMapper;

    @Override
    @Transactional
    public void saveDb(CouponRecord couponRecord) {
        logger.info("===========saveDb begin");
        couponRecordMapper.addCouponRecord(couponRecord);
        if(true){
            logger.error("===========saveDb fail");
            throw new RuntimeException("运行时异常！");
        }
        logger.info("===========saveDb success");
    }

    @Override
    public void saveCache() {

    }
}
