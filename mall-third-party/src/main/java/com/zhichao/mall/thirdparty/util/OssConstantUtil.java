package com.zhichao.mall.thirdparty.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description: oss初始化参数
 * @Author: zhichao
 * @Date: 2021/6/15 23:28
 */
@Component
public class OssConstantUtil implements InitializingBean {

    @Value("${alibaba.cloud.oss.endpoint}")
    private String endpoint;

    @Value("${alibaba.cloud.access-key}")
    private String accessKey;

    @Value("${alibaba.cloud.secret-key}")
    private String secretKey;

    @Value("${alibaba.cloud.oss.bucket}")
    private String bucketName;

    public static String END_POINT;
    public static String ACCESS_KEY_ID;
    public static String ACCESS_KEY_SECRET;
    public static String BUCKET_NAME;

    @Override
    public void afterPropertiesSet() throws Exception {
        END_POINT = endpoint;
        ACCESS_KEY_ID = accessKey;
        ACCESS_KEY_SECRET = secretKey;
        BUCKET_NAME = bucketName;
    }
}
