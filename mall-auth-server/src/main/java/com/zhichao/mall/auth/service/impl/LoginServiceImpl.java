package com.zhichao.mall.auth.service.impl;

import com.zhichao.mall.auth.constant.AuthServerConstant;
import com.zhichao.mall.auth.feign.ThirdPartFeignService;
import com.zhichao.mall.auth.service.LoginService;
import com.zhichao.mall.common.utils.R;
import com.zhichao.mall.common.utils.ResultCode;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 登陆页面逻辑层
 * @Author: zhichao
 * @Date: 2022/4/4 21:23
 */
@Service
public class LoginServiceImpl implements LoginService {

    /**
     * 发送短信第三方服务
     */
    @Autowired
    ThirdPartFeignService thirdPartFeignService;

    /**
     * redis
     */
    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 发送短信验证码
     *
     * @param phone
     */
    @Override
    public R sendCode(String phone) {

        String codeRedisKey = AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone;

        // 1. 验证redis中是否有短信验证码
        // 没有的话证明验证码过期，或者没有发送过
        // 存在的话证明已经发送过验证码
        String redisCode = redisTemplate.opsForValue().get(codeRedisKey);
        if (StringUtils.isNotEmpty(redisCode)) {
            return R.error(ResultCode.SMS_CODE_EXCEPTION.getCode(), "短信验证码发送太频繁，请稍后再试");
        }

        // 生成验证码
        String code = UUID.randomUUID().toString().substring(0, 5);
        R sendCodeResult = thirdPartFeignService.sendMsgCode(phone, code);
        if (sendCodeResult.getCode() != 0) {
            // 短信验证码发送失败
            return R.error(ResultCode.SMS_CODE_EXCEPTION.getCode(), ResultCode.SMS_CODE_EXCEPTION.getMessage());
        }
        redisTemplate.opsForValue().set(codeRedisKey, code, 1, TimeUnit.MINUTES);
        return R.ok();
    }
}
