package com.zhichao.mall.product.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 商品sku信息在es中存储的模型
 * @Author: zhichao
 * @Date: 2021/10/26 22:35
 */
@Data
public class SkuInfoEsModel {

    /**
     * 商品的skuId
     */
    private Long skuId;

    /**
     * 商品的spuId
     */
    private Long spuId;

    /**
     * 商品的sku标题
     */
    private String skuTitle;

    /**
     * 商品的sku的价格
     */
    private BigDecimal skuPrice;

    /**
     * 商品的sku图片地址
     */
    private String skuImg;

    /**
     * 商品的销售数量
     */
    private Long saleCount;

    /**
     * 库存是否还有商品
     */
    private Boolean hasStock;

    /**
     * 商品的热点分数
     */
    private Long hotScore;

    /**
     * 商品的品牌id
     */
    private Long brandId;

    /**
     * 商品的分类id
     */
    private Long catalogId;

    /**
     * 商品的品牌名称
     */
    private String brandName;

    /**
     * 商品的品牌图片的地址
     */
    private String brandImg;

    /**
     * 商品的分类名字
     */
    private String catalogName;

    /**
     * 商品的属性
     */
    private List<Attrs> attrs;

    @Data
    public static class Attrs {
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}
