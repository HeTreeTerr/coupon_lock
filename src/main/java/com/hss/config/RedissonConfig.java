package com.hss.config;

import com.hss.servicer.DistributedLocker;
import com.hss.servicer.impl.RedissonDistributedLocker;
import com.hss.util.RedissLockUtil;
import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.RedissonClient;
import org.redisson.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(Config.class)
public class RedissonConfig {

    private static Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${redisson.address}")
    private String redissonAddress;
    @Value("${redisson.password}")
    private String redissonPassword;
    @Value("${spring.redis.timeout}")
    private int timeout;
    @Value("${spring.redis.jedis.pool.min-idle}")
    private int minIdle;
    @Value("${spring.redis.jedis.pool.max-active}")
    private int maxActive;
    /**
     * 单机模式自动装配
     * @return
     */
    @Bean
    @ConditionalOnProperty(name="redisson.address")
    RedissonClient redissonSingle() {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(redissonAddress)
                .setTimeout(timeout)
                .setConnectionPoolSize(maxActive)
                .setConnectionMinimumIdleSize(minIdle)
                .setDatabase(0);

        if(null != redissonPassword && !"".equals(redissonPassword)) {
            serverConfig.setPassword(redissonPassword);
        }

        return Redisson.create(config);
    }

    /**
     * 装配locker类，并将实例注入到RedissLockUtil中
     * @return
     */
    @Bean
    DistributedLocker distributedLocker(RedissonClient redissonClient) {
        DistributedLocker locker = new RedissonDistributedLocker();
        locker.setRedissonClient(redissonClient);
        RedissLockUtil.setLocker(locker);
        return locker;
    }

}
