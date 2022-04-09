package com.zhichao.mall.auth.service;

import com.zhichao.mall.common.utils.R;

/**
 * @Description:
 * @Author: zhichao
 * @Date: 2022/4/4 21:21
 */
public interface LoginService {

    /**
     * 发送短信验证码
     *
     * @param phone
     * @return r
     */
    R sendCode(String phone);
}
