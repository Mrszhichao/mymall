package com.zhichao.mall.common.utils;

/***
 * @Description: 枚举了一些常用API操作码
 *    错误码和错误信息定义类 *
 *    1.错误码定义规则为 5 为数字
 *    2.前两位表示业务场景，最后三位表示错误码。例如：100001。10:通用 001:系统未知 异常
 *    错误码列表：
 *        10: 通用
 *           000：系统未知异常
 *           001：参数格式校验
 *           002：短息验证码异常
 *        11:    商品
 *        12:    订单
 *        13:    购物车
 *        14:    物流
 *        15:    用户模块
 * @Author: zhichao
 * @Date: 2021/6/19 16:43
 */

public enum ResultCode {
    /**
     * 系统未知异常
     */
    UNKNOWN_EXCEPTION(10000, "系统未知异常"),

    /**
     * 参数校验失败
     */
    VALID_EXCEPTION(10001, "参数检验失败"),

    /**
     * 参数校验失败
     */
    SMS_CODE_EXCEPTION(10002, "短信验证码发送异常"),

    /**
     * 用户注册手机号重复
     */
    PHONE_EXIST_EXCEPTION(15001, "手机号码重复"),

    /**
     * 用户名已经存在
     */
    USERNAME_EXIST_EXCEPTION(15001, "用户名已经存在"),

    /**
     * 商品上架失败
     */
    PRODUCT_UP_EXCEPTION(11002, "商品上架失败");

    private Integer code;
    private String message;

    private ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
