package com.zhichao.mall.thirdparty.controller;

import com.zhichao.mall.common.utils.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 短信发送
 * @Author: zhichao
 * @Date: 2022/3/28 00:33
 */
@RestController
@RequestMapping("/sms")
public class SmsSendController {

    /**
     * 发送短信验证码
     *
     * @param phone
     * @param code
     * @return
     */
    @GetMapping("/sendMsg")
    public R sendMsgCode(@RequestParam("phone") String phone, @RequestParam("code") String code) {
        return R.ok();
    }
}
