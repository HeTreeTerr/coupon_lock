package com.hss.controller.handWrittenDistributedLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 手动实现分布式锁
 */
@RestController
@Controller
@RequestMapping(value = "/buy_goods")
public class DistributedLockDemoController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/v1.0.0")
    public String buyGoodsV1_0_0(){
        logger.info("{}==buyGoods==V1_0_0",serverPort);
        return "success";
    }
}
