package com.zhichao.mall.product.controller;

import com.zhichao.mall.common.utils.R;
import com.zhichao.mall.product.entity.CategoryEntity;
import com.zhichao.mall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 商品三级分类
 *
 * @author zhichao
 * @date 2021-05-23 17:42:07
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    /**
     * 获取商品分类树
     */
    @RequestMapping("/list/tree")
    public R list(@RequestParam Map<String, Object> params) {
        // 获取所有商品分类，及其下属类
        List<CategoryEntity> list = categoryService.listWithTree();
        return R.ok().put("treeList", list);
    }


    /**
     * 获取当前商品的信息
     */
    @RequestMapping("/info/{catId}")
    public R info(@PathVariable("catId") Long catId) {
        CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("category", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryEntity category) {
        categoryService.save(category);

        return R.ok();
    }

    /**
     * 批量修改
     */
    @RequestMapping("/updateBatch")
    public R updateBatch(@RequestBody CategoryEntity[] categories) {
        categoryService.updateBatchById(Arrays.asList(categories));
        return R.ok();
    }

    /**
     * 修改商品分类和商品分类相关的信息
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryEntity category) {
        categoryService.updateRelationById(category);

        return R.ok();
    }

    /**
     * 删除商品分类
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] catIds) {
        // 检查当前节点是否可以被删除
        categoryService.removeMenuByIds(Arrays.asList(catIds));
        return R.ok();
    }

}
