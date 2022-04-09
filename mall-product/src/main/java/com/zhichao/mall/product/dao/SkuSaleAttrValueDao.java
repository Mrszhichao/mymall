package com.zhichao.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhichao.mall.product.entity.SkuSaleAttrValueEntity;
import com.zhichao.mall.product.vo.SkuItemSaleAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * sku销售属性&值
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 17:42:06
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    /**
     * 获取商品销售属性
     *
     * @param spuId
     * @return
     */
    List<SkuItemSaleAttrVo> getSaleAttrBySpuId(@Param("spuId") Long spuId);
}
