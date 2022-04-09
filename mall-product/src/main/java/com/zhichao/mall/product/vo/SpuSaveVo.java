/**
 * Copyright 2021 json.cn
 */
package com.zhichao.mall.product.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2021-07-19 22:32:10
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@Data
public class SpuSaveVo {

    private String spuName;
    private String spuDescription;
    private Long catalogId;
    private Long brandId;
    private BigDecimal weight;
    private int publishStatus;
    private List<String> decript;
    private List<String> images;
    private Bounds bounds;
    private List<BaseAttrs> baseAttrs;
    private List<Skus> skus;
}