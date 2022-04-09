package com.zhichao.mall.member.exception;

/**
 * @Description: 电话号存在异常
 * @Author: zhichao
 * @Date: 2022/4/5 15:12
 */
public class PhoneExistException extends RuntimeException {
    public PhoneExistException() {
        super("电话号码已经存在");
    }
}
