package com.zhichao.mall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 启动类
 *
 * @author zhichao
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class MallAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallAuthServerApplication.class, args);
    }

}
