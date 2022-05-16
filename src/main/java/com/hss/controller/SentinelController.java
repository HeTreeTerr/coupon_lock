package com.hss.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 哨兵模式特性测试
 */
@RestController
public class SentinelController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/test_sentinel")
    public void testSentinel(){
        int i = 1;
        while (true){
            stringRedisTemplate.opsForValue().set("test_sentinel" + i , String.valueOf(i));
            logger.info("设置key:{}","test_sentinel" + i);
            i++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.error("错误",e);
            }
        }
    }
}
