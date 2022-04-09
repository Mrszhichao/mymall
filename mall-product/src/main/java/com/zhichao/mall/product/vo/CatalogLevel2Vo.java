package com.zhichao.mall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 首页展示的二级分类显示的vo
 * @Author: zhichao
 * @Date: 2021/11/16 23:17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CatalogLevel2Vo {

    /**
     * 一级分类的id
     */
    private String catalog1Id;

    /**
     * 二级分类的id
     */
    private String id;

    /**
     * 二级分类的名称
     */
    private String name;

    /**
     * 对应的三级分类的集合
     */
    private List<Catalog3Vo> catalog3List;


    /**
     * 三级分类内部类
     */
    @Data
    public static class Catalog3Vo {
        /**
         * 二级分类的id
         */
        private String catalog2Id;

        /**
         * 三级分类的id
         */
        private String id;

        /**
         * 三级分类的名称
         */
        private String name;
    }
}
