package com.zhichao.mall.product.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Description: 缓存配置类
 * @Author: zhichao
 * @Date: 2021/11/30 00:03
 */
@Configuration
@EnableCaching
public class MyCacheConfig {

    /**
     * redis数据json形式保存
     *
     * @param cacheProperties
     * @return
     */
    @Bean
    RedisCacheConfiguration redisJsonCacheConfig(CacheProperties cacheProperties) {

        // 设置数据保存json
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericFastJsonRedisSerializer()));

        //保留redis原有的配置
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        if (redisProperties.getTimeToLive() != null) {
            cacheConfig = cacheConfig.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            cacheConfig = cacheConfig.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            cacheConfig = cacheConfig.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            cacheConfig = cacheConfig.disableKeyPrefix();
        }

        return cacheConfig;
    }
}
