package com.zhichao.mall.common.to;

import lombok.Data;

/**
 * @Description: 查询sku库存to
 * @Author: zhichao
 * @Date: 2021/11/3 22:43
 */
@Data
public class SkuHasStockTo {

    /**
     * sku id
     */
    private Long skuId;

    /**
     * 是否还有库存 true：有 false：没有
     */
    private Boolean hasStock;
}
