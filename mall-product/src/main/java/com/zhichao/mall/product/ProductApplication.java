package com.zhichao.mall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Description: 启动类
 * @Author: zhichao
 * @Date: 2021/6/12 13:06
 */
@SpringBootApplication(scanBasePackages = "com.zhichao.mall")
@MapperScan("com.zhichao.mall.product.dao")
@EnableDiscoveryClient
@EnableFeignClients
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }

}
