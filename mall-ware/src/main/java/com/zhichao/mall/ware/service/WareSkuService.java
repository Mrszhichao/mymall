package com.zhichao.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhichao.mall.common.to.SkuHasStockTo;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 18:55:44
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 采购成功后添加商品库存信息
     *
     * @param skuId  采购的商品id
     * @param wareId 仓库id
     * @param skuNum 商品数量
     */
    void addStock(Long skuId, Long wareId, Integer skuNum);

    /**
     * 查询商品库存
     *
     * @param skuIds
     * @return
     */
    List<SkuHasStockTo> getSkuHasStock(List<Long> skuIds);
}

