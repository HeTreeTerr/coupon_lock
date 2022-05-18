package com.hss.config;

import com.hss.servicer.DistributedLocker;
import com.hss.servicer.impl.RedissonDistributedLocker;
import com.hss.util.RedissLockUtil;
import org.redisson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@ConditionalOnClass(Config.class)
public class RedissonConfig {

    private static Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${redisson.address}")
    private String redissonAddress;

    @Value("${redisson.master-name}")
    private String redissonMasterName;

    @Value("${redisson.sentinel-addresses}")
    private String redissonSentinelAddresses;

    @Value("${redisson.cluster-addresses}")
    private String redissonClusterAddresses;

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
    @ConditionalOnProperty(name="spring.redis.mode",havingValue = "single")
    RedissonClient redissonSingle() {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(redissonAddress)
                .setTimeout(timeout)
                .setConnectionPoolSize(maxActive)
                .setConnectionMinimumIdleSize(minIdle)
                .setDatabase(0);

        if(!StringUtils.isEmpty(redissonPassword)) {
            serverConfig.setPassword(redissonPassword);
        }

        return Redisson.create(config);
    }

    /**
     * 哨兵模式
     * @return
     */
    @Bean
    @ConditionalOnProperty(name="spring.redis.mode",havingValue = "sentinel")
    RedissonClient redissonSentinel() {
        Config config = new Config();
        String[] nodes = redissonSentinelAddresses.split(",");
        List<String> newNodes = new ArrayList(nodes.length);

        Arrays.stream(nodes).forEach((index) -> newNodes.add(
                index));
        SentinelServersConfig serverConfig = config.useSentinelServers()
                .addSentinelAddress(newNodes.toArray(new String[0]))
                .setMasterName(redissonMasterName)
                .setReadMode(ReadMode.SLAVE)
                .setTimeout(timeout)
                .setMasterConnectionPoolSize(maxActive)
                .setSlaveConnectionPoolSize(maxActive);

        if (!StringUtils.isEmpty(redissonPassword)) {
            serverConfig.setPassword(redissonPassword);
        }
        return Redisson.create(config);
    }

    /**
     * 集群模式
     * @return
     */
    @Bean
    @ConditionalOnProperty(name="spring.redis.mode",havingValue = "cluster")
    RedissonClient redissonCluster() {
        Config config = new Config();

        String[] nodes = redissonClusterAddresses.split(",");
        List<String> newNodes = new ArrayList(nodes.length);

        Arrays.stream(nodes).forEach((index) -> newNodes.add(
                index));
        ClusterServersConfig clusterServersConfig = config.useClusterServers()
                .addNodeAddress(newNodes.toArray(new String[0]));
        if (!StringUtils.isEmpty(redissonPassword)) {
            clusterServersConfig.setPassword(redissonPassword);
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
