package com.zhichao.mall.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Description: 登首页控制层
 * @Author: zhichao
 * @Date: 2022/3/27 23:30
 */
@Controller
public class IndexController {

    @GetMapping({"/", "/login.html"})
    public String toLogin() {
        return "login";
    }

    @GetMapping("/register.html")
    public String toRegister() {
        return "register";
    }
}
