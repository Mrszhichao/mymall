package com.zhichao.mall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description: 采购单合并
 * @Author: zhichao
 * @Date: 2021/7/25 18:07
 */
@Data
public class MergePurchaseVo {

    private Long purchaseId;
    private List<Long> items;
}
