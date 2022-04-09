package com.zhichao.mall.product.vo;

import com.zhichao.mall.product.entity.AttrEntity;
import com.zhichao.mall.product.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

/**
 * @Description: 商品分组的属性展示vo
 * @Author: zhichao
 * @Date: 2021/7/11 23:21
 */
@Data
public class AttrGroupWithAttrsVo extends AttrGroupEntity {

    /**
     * 商品分组的属性
     */
    private List<AttrEntity> attrs;
}
