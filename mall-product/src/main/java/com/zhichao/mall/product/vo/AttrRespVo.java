package com.zhichao.mall.product.vo;

import lombok.Data;

/**
 * @Description: 与属性相关联的信息
 * @Author: zhichao
 * @Date: 2021/6/27 20:27
 */
@Data
public class AttrRespVo extends AttrVo {

    /**
     * 所属分类名字
     */
    private String catelogName;
    
    /**
     * 所属分组名字
     */
    private String groupName;

    private Long[] catelogPath;
}
