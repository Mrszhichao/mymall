package com.zhichao.mall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Description: 商品所对应的商品属性vo
 * @Author: zhichao
 * @Date: 2022/3/8 23:09
 */
@Data
@ToString
public class SpuItemSaleAttrVo {
    private String groupName;
    private List<SpuBaseAttrVo> spuBaseAttrVos;
}
