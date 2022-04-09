package com.zhichao.mall.common.constant;

/**
 * @Description: 仓库相关常量
 * @Author: zhichao
 * @Date: 2021/7/11 22:27
 */
public class WareConstant {

    /**
     * 合并采购单的状态
     */
    public enum PurchaseStatusEnum {
        /**
         * 新建狀態
         */
        CREATED(0, "新建"),
        /**
         * 已分配狀態
         */
        ASSIGNED(1, "已分配"),
        /**
         * 已领取狀態
         */
        RECEIVE(2, "已领取"),
        /**
         * 已完成狀態
         */
        FINISH(3, "已完成"),
        /**
         * 有异常狀態
         */
        HASERROR(4, "有异常");

        private int code;
        private String msg;

        PurchaseStatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    /**
     * 采购需求
     */
    public enum PurchaseDetailStatusEnum {
        /**
         * 新建狀態
         */
        CREATED(0, "新建"),
        /**
         * 已分配狀態
         */
        ASSIGNED(1, "已分配"),
        /**
         * 正在采购狀態
         */
        BUYING(2, "正在采购"),
        /**
         * 已完成狀態
         */
        FINISH(3, "已完成"),
        /**
         * 采购失败狀態
         */
        HASERROR(4, "采购失败");

        private int code;
        private String msg;

        PurchaseDetailStatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
