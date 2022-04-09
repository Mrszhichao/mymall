package com.zhichao.mall.common.constant;

/**
 * @Description: 商品相关常量
 * @Author: zhichao
 * @Date: 2021/7/11 22:27
 */
public class ProductConstant {

    /**
     * 商品属性
     */
    public enum AttrEnum {
        /**
         * 基本属性
         */
        ATTR_TYPE_BASE(1, "基本属性"),
        /**
         * 销售属性
         */
        ATTR_TYPE_SALE(0, "销售属性");

        private int code;
        private String message;

        AttrEnum(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * 商品的状态
     */
    public enum StatusEnum {
        /**
         * 新建商品
         */
        NEW_SPU(0, "新建"),
        /**
         * 上架商品
         */
        SPU_UP(1, "上架"),
        /**
         * 下架商品
         */
        SPU_DOWN(2, "下架");

        private int code;
        private String message;

        StatusEnum(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
