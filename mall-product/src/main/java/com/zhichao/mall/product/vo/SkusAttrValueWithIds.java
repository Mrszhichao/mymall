package com.zhichao.mall.product.vo;

import lombok.Data;

/**
 * @Description: 商品属性值和其对应的商品id
 * @Author: zhichao
 * @Date: 2022/3/20 21:34
 */
@Data
public class SkusAttrValueWithIds {
    private String attrValue;
    private String skuIds;
}
