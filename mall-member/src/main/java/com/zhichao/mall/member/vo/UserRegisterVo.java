package com.zhichao.mall.member.vo;

import lombok.Data;

/**
 * @Description: 会员注册
 * @Author: zhichao
 * @Date: 2022/4/5 12:26
 */
@Data
public class UserRegisterVo {

    private String userName;

    private String password;

    private String phoneNum;

    private String code;
}
