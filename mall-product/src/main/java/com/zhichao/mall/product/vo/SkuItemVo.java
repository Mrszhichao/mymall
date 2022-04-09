package com.zhichao.mall.product.vo;

import com.zhichao.mall.product.entity.SkuImagesEntity;
import com.zhichao.mall.product.entity.SkuInfoEntity;
import com.zhichao.mall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @Description: 商品详情展示Vo
 * @Author: zhichao
 * @Date: 2022/3/8 22:52
 */
@Data
public class SkuItemVo {
    /**
     * 1.sku的基本信息
     */
    private SkuInfoEntity skuInfoEntity;

    private Boolean hasStock = true;

    /**
     * 2.sku的图片信息
     */
    private List<SkuImagesEntity> skuImagesEntities;

    /**
     * 3.sku的销售属性组合
     */
    private List<SkuItemSaleAttrVo> skuItemSaleAttrVo;

    /**
     * 4.spu的介绍
     */
    private SpuInfoDescEntity spuInfoDescEntity;

    /**
     * 5.sku的规格参数
     */
    private List<SpuItemSaleAttrVo> spuItemSaleAttrVos;
}
