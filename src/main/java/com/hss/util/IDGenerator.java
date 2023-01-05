package com.hss.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * 通过redis生成分布式自增唯一id
 */
@Component
public class IDGenerator {

    @Autowired
    private RedisTemplate redisTemplate;

    public long nextId(String businessKey){
        //1. 第 2 段，当前日期
        long today = Long.parseLong(LocalDate.now(ZoneId.of("Asia/Shanghai")).toString().replace("-",""));

        //2. 第 3 段，递增序列号
        Long no = redisTemplate.opsForValue().increment(businessKey + "_id_key");
        return today << 32 | no;
    }

}
