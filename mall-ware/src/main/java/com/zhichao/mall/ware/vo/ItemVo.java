package com.zhichao.mall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description: 采购完成细节项
 * @Author: zhichao
 * @Date: 2021/7/25 23:36
 */
@Data
public class ItemVo {

    @NotNull
    private Long itemId;
    private Integer status;
    private String reason;
}
