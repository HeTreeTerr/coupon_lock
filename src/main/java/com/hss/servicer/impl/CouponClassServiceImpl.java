package com.hss.servicer.impl;

import com.hss.bean.CouponClass;
import com.hss.mapper.CouponClassMapper;
import com.hss.servicer.CouponClassService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CouponClassServiceImpl implements CouponClassService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CouponClassMapper couponClassMapper;

    @Override
    public CouponClass findCouponClass(CouponClass couponClass) {
        return couponClassMapper.findCouponClass(couponClass);
    }

    @Override
    public CouponClass findCouponClassById(Long id) {
        return couponClassMapper.findCouponClassById(id);
    }
}
