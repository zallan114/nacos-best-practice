package com.example.consumer.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.github.benmanes.caffeine.cache.Caffeine;
@Configuration
@EnableCaching //使用 @EnableCaching 注解开启Spring的缓存支持
public class RedisCacheConfig {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheConfig.class);

    @Value("${spring.cache.enabled:true}")
    private boolean cacheEnabled;

    @Value("${spring.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Value("${spring.redis.password:}")
    private String redisPassword;

    @Value("${spring.redis.database:0}")
    private int redisDatabase;

    //Spring Cache 默认只支持单一 CacheManager，需自定义 @MultiLevelCache 注解 + AOP 实现双层查询：

    // 本地缓存管理器（Caffeine）
    @Bean
    @ConditionalOnProperty(value = "spring.cache.enabled", havingValue = "false", matchIfMissing = false)
    @Primary // 优先查本地缓存
    public CacheManager caffeineCacheManager() {
        log.info("Redis缓存未启用，使用基于内存的caffeineCacheManager");
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(1000)
                .expireAfterWrite(5, TimeUnit.MINUTES) // 本地缓存5分钟过期
                .recordStats());
        return cacheManager;
    }

    // 当缓存未启用或Redis不可用时，提供基于内存的简单缓存管理器作为备选
    // @Bean
    // @ConditionalOnProperty(value = "spring.cache.enabled", havingValue = "false", matchIfMissing = false)
    // @Primary
    // public CacheManager simpleCacheManager() {
    //     log.info("Redis缓存未启用，使用基于内存的SimpleCacheManager");
    //     return new ConcurrentMapCacheManager("productCache", "orderCache");
    // }


    // 配置Redis连接工厂，仅在缓存启用时创建
    @Bean
    @ConditionalOnProperty(value = "spring.cache.enabled", havingValue = "true", matchIfMissing = true)
    public RedisConnectionFactory redisConnectionFactory() {
        try {
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
            config.setDatabase(redisDatabase);
            if (!redisPassword.isEmpty()) {
                config.setPassword(redisPassword);
            }
            //Lettuce客户端的一个重要特性是 延迟连接 （Lazy Connection）
            // 只有在第一次访问Redis时才会建立连接，后续请求会复用已有的连接
            LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
            // 设置连接超时时间
            factory.setTimeout(5000);
            return factory;
        } catch (Exception e) {
            log.warn("Redis连接配置失败，将使用默认连接参数: {}", e.getMessage());
            // 返回默认配置，允许应用继续启动
            return new LettuceConnectionFactory();
        }
    }

    // 配置缓存管理器（设置过期时间、序列化方式），仅在缓存启用时创建
    @Bean
    @ConditionalOnProperty(value = "spring.cache.enabled", havingValue = "true", matchIfMissing = true)
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30)) // 默认缓存30分钟
            //缓存键序列化方式： StringRedisSerializer
            // 缓存键（key）的序列化器，用于将缓存键转换为字节数组存储在Redis中。
            // StringRedisSerializer 是一个简单的序列化器，将键直接转换为字符串并进行编码。
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            //缓存值序列化方式： GenericJackson2JsonRedisSerializer
            // 缓存值（value）的序列化器，用于将缓存值转换为字节数组存储在Redis中。
            // GenericJackson2JsonRedisSerializer 是一个基于Jackson库的序列化器，
            // 可以将任意Java对象转换为JSON字符串并进行编码。
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
            .disableCachingNullValues(); // 不缓存null值

        // 针对热点数据设置不同过期时间（如订单数据缓存10分钟）
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("orderCache", config.entryTtl(Duration.ofMinutes(10)));
        cacheConfigurations.put("productCache", config.entryTtl(Duration.ofHours(1)));

        //具体使用， 参考 DianProductsResource.java 240-260  ： @Cacheable 是Spring框架提供的缓存注解
        // 缓存订单数据
        // cacheManager.putCache("orderCache", orderCache);

        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(config)
            .withInitialCacheConfigurations(cacheConfigurations)
            .build();
    }

    

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        // Key 序列化：String 序列化（避免 key 乱码）
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        // Value 序列化：Jackson2Json 序列化（支持对象存储，避免乱码）
        GenericJackson2JsonRedisSerializer jacksonSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setValueSerializer(jacksonSerializer);
        redisTemplate.setHashValueSerializer(jacksonSerializer);

        // 初始化 RedisTemplate
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
