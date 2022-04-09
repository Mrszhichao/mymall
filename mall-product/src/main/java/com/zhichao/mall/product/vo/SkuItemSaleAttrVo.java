package com.zhichao.mall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description: sku的销售属性vo
 * @Author: zhichao
 * @Date: 2022/3/8 23:01
 */
@Data
public class SkuItemSaleAttrVo {
    private Long attrId;
    private String attrName;
    private List<SkusAttrValueWithIds> attrValues;
}
