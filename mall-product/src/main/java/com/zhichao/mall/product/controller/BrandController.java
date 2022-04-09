package com.zhichao.mall.product.controller;

import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.common.utils.R;
import com.zhichao.mall.product.entity.BrandEntity;
import com.zhichao.mall.product.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 品牌
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 17:42:07
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 获得指定的品牌
     *
     * @param brandId
     * @return
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId) {
        BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@Validated @RequestBody BrandEntity brand) {
        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改品牌信息以及其关联表的信息
     */
    @RequestMapping("/update")
    public R update(@RequestBody BrandEntity brand) {
        brandService.updateRelationById(brand);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] brandIds) {
        brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

    /**
     * 根据分类获取品牌
     *
     * @return
     */
    @GetMapping("catBrands/list")
    public R getBrandList(@RequestParam Long catId) {
        List<BrandEntity> brands = brandService.getBrandsByCatelogId(catId);
        return R.ok().put("data", brands);
    }
}
