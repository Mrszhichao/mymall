package com.zhichao.mall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: Redisson配置类
 * @Author: zhichao
 * @Date: 2021/11/24 21:38
 */
@Configuration
public class MyRedissonConfig {

    /**
     * 配置redisson
     *
     * @return
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://r-2zevhltnqzp36nnxcvpd.redis.rds.aliyuncs.com:6379")
                .setPassword("Czc193590");
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
