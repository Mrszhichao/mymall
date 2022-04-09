package com.zhichao.mall.product.web;

import com.zhichao.mall.product.entity.CategoryEntity;
import com.zhichao.mall.product.service.CategoryService;
import com.zhichao.mall.product.vo.CatalogLevel2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @Description: 商城首页控制层
 * @Author: zhichao
 * @Date: 2021/11/15 23:00
 */
@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 查询商品一级分类
     *
     * @param model
     * @return
     */
    @GetMapping({"/", "/index.html"})
    public String initIndexPage(Model model) {
        List<CategoryEntity> level1Categories = categoryService.getLevel1Categories();
        model.addAttribute("categories", level1Categories);
        return "login";
    }


    @ResponseBody
    @GetMapping("index/catalog.json")
    public Map<String, List<CatalogLevel2Vo>> getLowerCatalogJson() {
        Map<String, List<CatalogLevel2Vo>> result = (Map<String, List<CatalogLevel2Vo>>) categoryService.getCatalogJson();
        return result;
    }

}
