package com.zhichao.mall.search.controller;

import com.zhichao.mall.search.service.MallSearchService;
import com.zhichao.mall.search.vo.SearchParam;
import com.zhichao.mall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Description: 商品查询控制类
 * @Author: zhichao
 * @Date: 2022/2/15 22:34
 */
@Controller
public class SearchController {

    @Autowired
    private MallSearchService searchService;

    /**
     * 商品查询页面展示
     *
     * @return
     */
    @GetMapping("/list.html")
    public String showSearchPage(SearchParam param, Model model) {

        SearchResult searchResult = searchService.search(param);
        model.addAttribute("result", searchResult);
        return "index";
    }

}
