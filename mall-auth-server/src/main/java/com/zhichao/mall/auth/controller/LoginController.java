package com.zhichao.mall.auth.controller;

import com.zhichao.mall.auth.service.LoginService;
import com.zhichao.mall.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 登录页面控制层
 * @Author: zhichao
 * @Date: 2022/4/4 21:14
 */
@RestController
public class LoginController {

    /**
     * 登录页面service
     */
    @Autowired
    LoginService loginService;

    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phoneNum") String phone) {
        return loginService.sendCode(phone);
    }

    // 第三方登录（OAuth2.0）
    // 1.向用户申请请求登录         第三方应用➡服务器（认证）
    // 2.用户授权（输入账户和密码）  服务器（认证）➡服务器（认证）
    // 3.使用上步授权，进行认证      服务器（认证）➡服务器（认证）
    // 4.认证通过，返回访问令牌      服务器（认证）➡第三方应用
    // 5.使用访问令牌获取开放信息    三方应用➡服务器（认证）
    // 6.认证令牌，返回受保护信息    服务器（认证）➡第三方应用
    // 使用code换取accesstoken只能换取一次
    //
}
