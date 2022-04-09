package com.zhichao.mall.product.controller;

import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.common.utils.R;
import com.zhichao.mall.product.entity.SpuInfoEntity;
import com.zhichao.mall.product.service.SpuInfoService;
import com.zhichao.mall.product.vo.SpuSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * spu信息
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 17:42:06
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;


    /**
     * 展示所有spu信息
     *
     * @param params
     * @return
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = spuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }

    /**
     * 商品上架
     */
    @PostMapping("/{spuId}/up")
    public R spuUp(@PathVariable("spuId") Long spuId) {
        spuInfoService.upShelf(spuId);
        return R.ok();
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SpuSaveVo vo) {
        spuInfoService.saveSpuInfo(vo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SpuInfoEntity spuInfo) {
        spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
