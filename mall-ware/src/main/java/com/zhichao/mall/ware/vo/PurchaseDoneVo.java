package com.zhichao.mall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description: 采购完成
 * @Author: zhichao
 * @Date: 2021/7/25 23:35
 */
@Data
public class PurchaseDoneVo {

    private Long id;

    private List<ItemVo> items;
}
