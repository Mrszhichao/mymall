package com.zhichao.mall.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description: 线程池元数据配置
 * @Author: zhichao
 * @Date: 2022/3/25 21:48
 */
@Component
@ConfigurationProperties(prefix = "mall.thread")
@Data
public class ThreadPoolConfigProperties {
    private Integer coreSize;
    private Integer maxSize;
    private Integer keepAliveTime;
}
