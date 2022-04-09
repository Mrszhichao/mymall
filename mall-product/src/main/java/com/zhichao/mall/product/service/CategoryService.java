package com.zhichao.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.product.entity.CategoryEntity;
import com.zhichao.mall.product.vo.CatalogLevel2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 17:42:07
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);


    /**
     * 获取商品分类和其对应的子分类
     *
     * @return 商品分类列表
     */
    List<CategoryEntity> listWithTree();

    /**
     * 删除当前商品种别
     *
     * @param ids 商品id
     */
    void removeMenuByIds(List<Long> ids);

    /**
     * 获取当前商品种别各个层级的id
     *
     * @param catelogId
     * @return
     */
    Long[] getCateGoryPath(Long catelogId);

    /**
     * 更新商品种别相关信息
     *
     * @param category
     */
    void updateRelationById(CategoryEntity category);


    /**
     * 获取一级分类
     *
     * @return
     */
    List<CategoryEntity> getLevel1Categories();

    /**
     * 获取或有二级分类和其下的三级分类
     *
     * @return
     */
    Map<String, List<CatalogLevel2Vo>> getCatalogJson();
}

