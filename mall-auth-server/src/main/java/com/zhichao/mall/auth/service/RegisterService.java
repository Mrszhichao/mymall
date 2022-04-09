package com.zhichao.mall.auth.service;

import com.zhichao.mall.auth.vo.UserRegisterVo;

/**
 * @Description: 会员注册
 * @Author: zhichao
 * @Date: 2022/4/5 11:56
 */
public interface RegisterService {

    /**
     * 会员注册
     *
     * @param vo
     */
    void register(UserRegisterVo vo);
}
