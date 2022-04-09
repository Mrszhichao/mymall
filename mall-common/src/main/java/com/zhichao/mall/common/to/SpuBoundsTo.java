package com.zhichao.mall.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: zhichao
 * @Date: 2021/7/25 09:53
 */
@Data
public class SpuBoundsTo {

    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
