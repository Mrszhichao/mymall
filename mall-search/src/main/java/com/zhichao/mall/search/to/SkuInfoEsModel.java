package com.zhichao.mall.search.to;

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

    /*
    "mappings": {
        "properties": {
            "skuId": {
            "type": "long"
            },
            "spuId": {
            "type": "keyword"
            },
            "skuTitle": {
            "type": "text",
            "analyzer": "ik_smart"
            },
            "skuPrice": {
            "type": "keyword"
            },
            "skuImg": {
            "type": "keyword","index": false,
            "doc_values": false
            },
            "saleCount": {
            "type": "long"
            },
            "hasStock": {
            "type": "boolean"
            },
            "hotScore": {
            "type": "long"
            },
            "brandId": {
            "type": "long"
            },
            "catalogId": {
            "type": "long"
            },
            "brandName": {
            "type": "keyword",
            "index": false,
            "doc_values": false
            },
            "brandImg": {
            "type": "keyword",
            "index": false,
            "doc_values": false
            },
            "catalogName": {
            "type": "keyword",
            "index": false,
            "doc_values": false
            },
            "attrs": {
                "type": "nested",
                "properties": {
                    "attrId": {
                        "type": "long"
                     },
                     "attrName": {},
                     "type" : "keyword",
                     "index": false,
                     "doc_values": false,
                     "attrValue": {
                        "type": "keyword"
                      }
                 }
            }
        }
    }
     */

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
