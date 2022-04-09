package com.zhichao.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.product.entity.BrandEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 17:42:07
 */
public interface BrandService extends IService<BrandEntity> {

    /**
     * 查询品牌数据
     *
     * @param params
     * @return
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 更新信息以及其关联的信息
     *
     * @param brand
     */
    void updateRelationById(BrandEntity brand);
    
    /**
     * 获取该分类的可能品牌
     *
     * @param catId
     * @return
     */
    List<BrandEntity> getBrandsByCatelogId(Long catId);
}

