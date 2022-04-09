package com.zhichao.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhichao.mall.product.entity.CategoryBrandRelationEntity;

import java.util.List;

/**
 * 品牌分类关联
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 17:42:07
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {


    /**
     * 根据品牌id查询旗下的商品种别
     *
     * @param brandId
     * @return
     */
    List<CategoryBrandRelationEntity> getCatelogList(Long brandId);

    /**
     * 保存商品关联信息
     *
     * @param categoryBrandRelation
     */
    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    /**
     * 更新品牌相关信息
     *
     * @param brandId
     * @param name
     */
    void updateBrand(Long brandId, String name);

    /**
     * 更新商品信息
     *
     * @param catId
     * @param name
     */
    void updateCategory(Long catId, String name);


    /**
     * 根据分类id得到品牌id
     *
     * @param catId
     * @return
     */
    List<Long> getBrandIdsByCatelogId(Long catId);
}

