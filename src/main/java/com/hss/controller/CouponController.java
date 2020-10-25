package com.hss.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Controller
public class CouponController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/getCoupon")
    public String getCoupon(){

        return "我要抢券";
    }
}
