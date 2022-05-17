package com.hss.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@EnableCaching//开启缓存
@Configuration
public class RedisConfig {

    private static Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.sentinel.master}")
    private String sentinelMaster;

    @Value("${spring.redis.sentinel.nodes}")
    private String sentinelNodes;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.database}")
    private int database;

    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.jedis.pool.min-idle}")
    private int minIdle;

    @Value("${spring.redis.jedis.pool.max-wait}")
    private long maxWaitMillis;

    @Value("${spring.redis.jedis.pool.max-active}")
    private int maxActive;

    /**
     * 设置缓存管理器，这里可以配置默认过期时间等
     *
     * @param jedisConnectionFactory 连接池
     * @return
     */
    @Bean
    public CacheManager cacheManager(JedisConnectionFactory jedisConnectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(60));
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(jedisConnectionFactory);
        RedisCacheManager manager = new RedisCacheManager(redisCacheWriter, redisCacheConfiguration);
        return manager;
    }

    @SuppressWarnings("all")
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        RedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
//        支持事务
        template.setEnableTransactionSupport(true);
        return template;
    }

    /**
     * 单机模式
     * @return
     */
    @Bean
    @ConditionalOnProperty(name="spring.redis.mode",havingValue = "single")
    public JedisConnectionFactory jedisConnectionFactorySingle() {
        logger.info("jedisConnectionFactory:初始化了");
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        config.setMaxTotal(maxActive);
        //链接耗尽时是否阻塞，默认true
        config.setBlockWhenExhausted(true);
        //是否启用pool的jmx管理功能，默认true
        config.setJmxEnabled(true);
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setPoolConfig(config);
        factory.setHostName(host);
        factory.setPort(port);
        factory.setPassword(password);
        factory.setDatabase(database);
        factory.setTimeout(timeout);
        return factory;
    }

    /**
     * 哨兵模式
     * @return
     */
    @Bean
    @ConditionalOnProperty(name="spring.redis.mode",havingValue = "sentinel")
    public JedisConnectionFactory jedisConnectionFactorySentinel() {
        logger.info("jedisConnectionFactory:初始化了");
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        poolConfig.setMaxTotal(maxActive);
        //链接耗尽时是否阻塞，默认true
        poolConfig.setBlockWhenExhausted(true);
        //是否启用pool的jmx管理功能，默认true
        poolConfig.setJmxEnabled(true);

        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration();
        sentinelConfig.setDatabase(database);
        sentinelConfig.setMaster(sentinelMaster);
        sentinelConfig.setPassword(password);
        String[] nodes = sentinelNodes.split(",");
        List<String> nodeList = Arrays.asList(nodes);
        for (String node : nodeList){
            sentinelConfig.addSentinel(new RedisNode(node.split(":")[0],Integer.valueOf(node.split(":")[1])));
        }

        JedisConnectionFactory factory = new JedisConnectionFactory(sentinelConfig,poolConfig);
        factory.setTimeout(timeout);
        return factory;
    }

}
