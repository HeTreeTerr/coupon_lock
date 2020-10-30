package com.hss.controller;

import com.hss.bean.CouponRecord;
import com.hss.servicer.CouponRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Controller
public class CouponController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CouponRecordService couponRecordService;

    @RequestMapping(value = "/grabCouponRecordNoneLock")
    public String grabCouponRecordNoneLock(@RequestParam(value = "userName",required = true) String userName,
                                         @RequestParam(value = "secretKey",required = true) String secretKey){
        logger.info("开始抢券 threadName-->" + Thread.currentThread().getName());
        CouponRecord couponRecord = couponRecordService.grabCouponRecordNoneLock(userName, secretKey);
        if(null != couponRecord){
            logger.info("抢券成功 threadName-->" + Thread.currentThread().getName() + "seqNo-->" + couponRecord.getSeqNo());
            return "抢券成功 seqNo-->" + couponRecord.getSeqNo();
        }
        logger.info("已经抢光了 threadName-->" + Thread.currentThread().getName());
        return "已经抢光了";
    }

    @RequestMapping(value = "/grabCouponRecordDbLock")
    public String grabCouponRecordDbLock(@RequestParam(value = "userName",required = true) String userName,
                            @RequestParam(value = "secretKey",required = true) String secretKey){
        logger.info("开始抢券 threadName-->" + Thread.currentThread().getName());
        CouponRecord couponRecord = couponRecordService.grabCouponRecordDbLock(userName, secretKey);
        if(null != couponRecord){
            logger.info("抢券成功 threadName-->" + Thread.currentThread().getName() + "seqNo-->" + couponRecord.getSeqNo());
            return "抢券成功 seqNo-->" + couponRecord.getSeqNo();
        }
        logger.info("已经抢光了 threadName-->" + Thread.currentThread().getName());
        return "已经抢光了";
    }
}
