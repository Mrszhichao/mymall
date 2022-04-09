package com.zhichao.mall.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: sku优惠信息
 * @Author: zhichao
 * @Date: 2021/7/25 10:07
 */
@Data
public class SkuReductionTo {

    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
