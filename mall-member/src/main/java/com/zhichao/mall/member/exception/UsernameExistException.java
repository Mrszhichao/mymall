package com.zhichao.mall.member.exception;

/**
 * @Description: 用户名存在异常
 * @Author: zhichao
 * @Date: 2022/4/5 15:13
 */
public class UsernameExistException extends RuntimeException {

    public UsernameExistException() {
        super("用户名已经存在异常");
    }
}
