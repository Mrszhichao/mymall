package com.zhichao.mall.search.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Description: 检索首页展示
 * @Author: zhichao
 * @Date: 2022/2/9 23:57
 */
@Controller
public class IndexController {

    /**
     * 查询商品页面展示
     *
     * @return
     */
    @GetMapping({"/", "/index.html"})
    public String initIndexPage(Model model) {
        return "redirect:/list.html";
    }
}
