package com.zhichao.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhichao.mall.common.to.SkuReductionTo;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 18:52:54
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存sku的优惠满减信息
     *
     * @param skuReductionTo
     */
    void saveSkuReduction(SkuReductionTo skuReductionTo);
}

