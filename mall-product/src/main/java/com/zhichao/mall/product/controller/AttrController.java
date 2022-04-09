package com.zhichao.mall.product.controller;

import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.common.utils.R;
import com.zhichao.mall.product.service.AttrService;
import com.zhichao.mall.product.vo.AttrRespVo;
import com.zhichao.mall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * 商品属性
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 17:42:07
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {

    @Autowired
    private AttrService attrService;


    // /product/attr/base/listforspu/{spuId}

    /**
     * 根据条件获取分页详细信息
     *
     * @param params
     * @param catelogId
     * @param attrType
     * @return
     */
    @GetMapping("{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String, Object> params,
                          @PathVariable("catelogId") Long catelogId,
                          @PathVariable("attrType") String attrType) {
        PageUtils page = attrService.queryBaseAttrList(params, catelogId, attrType);
        return R.ok().put("page", page);
    }


    /**
     * 根据id获取属性信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId) {
        AttrRespVo attr = attrService.getAttrInfo(attrId);
        return R.ok().put("attr", attr);
    }

    /**
     * 保存属性以及属性相关信息
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVo attr) {
        attrService.saveAttr(attr);
        return R.ok();
    }

    /**
     * 修改属性信息
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVo attr) {
        attrService.updateAttr(attr);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds) {
        attrService.removeAttrByIds(attrIds);
        return R.ok();
    }

}
