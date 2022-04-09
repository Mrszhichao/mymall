package com.zhichao.mall.product.controller;

import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.common.utils.R;
import com.zhichao.mall.product.entity.AttrEntity;
import com.zhichao.mall.product.entity.AttrGroupEntity;
import com.zhichao.mall.product.service.AttrAttrgroupRelationService;
import com.zhichao.mall.product.service.AttrGroupService;
import com.zhichao.mall.product.service.AttrService;
import com.zhichao.mall.product.service.CategoryService;
import com.zhichao.mall.product.vo.AttrGroupRelationVo;
import com.zhichao.mall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 属性分组
 *
 * @author zhichao
 * @date 2021-05-23 17:42:07
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {

    @Autowired
    private AttrGroupService attrGroupService;

    /**
     * 商品种别service
     */
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService relationService;

    /**
     * 获取分类属性分组的详细信息
     *
     * @param params    分页以及检索key
     * @param catelogId 商品id
     * @return
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable Long catelogId) {
        PageUtils page = attrGroupService.queryPageByCatelogId(params, catelogId);
        return R.ok().put("page", page);
    }

    /**
     * 获取属性分组的关联的属性信息
     *
     * @param attrgroupId
     * @return
     */
    @GetMapping("/{attrgroupId}/attr/relation")
    public R getAttrRelation(@PathVariable Long attrgroupId) {
        List<AttrEntity> result = attrService.getRelationAttr(attrgroupId);
        return R.ok().put("data", result);
    }

    /**
     * 获取属性分组的未关联的属性信息
     *
     * @param attrgroupId
     * @return
     */
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R getAttrNoRelation(@PathVariable Long attrgroupId,
                               @RequestParam Map<String, Object> params) {
        PageUtils page = attrService.getNoRelationAttr(params, attrgroupId);
        return R.ok().put("page", page);
    }

    /**
     * 新增属性关联
     *
     * @param vos
     * @return
     */
    @PostMapping("/attr/relation")
    public R addAttrRelation(@RequestBody List<AttrGroupRelationVo> vos) {
        relationService.saveBatch(vos);
        return R.ok();
    }

    /**
     * 获取当前属性分类的信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long[] cateGoryPath = categoryService.getCateGoryPath(attrGroup.getCatelogId());
        attrGroup.setCatelogPath(cateGoryPath);
        return R.ok().put("attrGroup", attrGroup);
    }


    /**
     * 移除关联的属性
     *
     * @param vos
     * @return
     */
    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVo[] vos) {
        relationService.deleteAttrRelation(vos);
        return R.ok();
    }

    /**
     * 查询分类对应的属性分组和相关属性
     *
     * @param catelogId
     * @return
     */
    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId) {
        //1、查出当前分类下的所有属性分组，
        //2、查出每个属性分组的所有属性
        List<AttrGroupWithAttrsVo> vos = attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
        return R.ok().put("data", vos);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
