package com.zhichao.mall.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @Description: 用户注册用view object
 * @Author: zhichao
 * @Date: 2022/4/5 09:20
 */
@Data
public class UserRegisterVo {

    @NotEmpty(message = "用户名不能为空")
    @Pattern(regexp = "^[\\u4E00-\\u9FA5A-Za-z0-9_]+$", message = "请输入中文、英文、数字包括下划线")
    private String userName;

    @NotEmpty(message = "密码不能为空")
    @Length(min = 6, max = 20, message = "密码位数不正确")
    private String password;

    @NotEmpty(message = "电话号码不能为空")
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$", message = "手机号格式不正确")
    private String phoneNum;

    @NotEmpty(message = "验证码不能为空")
    private String code;

}
