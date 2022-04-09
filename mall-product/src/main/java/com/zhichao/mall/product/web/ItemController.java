package com.zhichao.mall.product.web;

import com.zhichao.mall.product.service.SkuInfoService;
import com.zhichao.mall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

/**
 * @Description: 商品详情控制层
 * @Author: zhichao
 * @Date: 2022/3/8 23:18
 */
@Controller
public class ItemController {

    @Autowired
    SkuInfoService skuInfoService;

    /**
     * 展示当前商品详情
     *
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = skuInfoService.getSkuDetailInfo(skuId);
        model.addAttribute("skuItem", skuItemVo);
        return "item";
    }
}
