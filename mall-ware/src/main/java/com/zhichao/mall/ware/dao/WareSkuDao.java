package com.zhichao.mall.ware.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhichao.mall.ware.entity.WareSkuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 18:55:44
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    /**
     * 添加商品库存信息
     *
     * @param skuId
     * @param wareId
     * @param skuNum
     */
    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    /**
     * 查实商品库存
     *
     * @param skuId
     * @return
     */
    Long getSkuStock(Long skuId);
}
