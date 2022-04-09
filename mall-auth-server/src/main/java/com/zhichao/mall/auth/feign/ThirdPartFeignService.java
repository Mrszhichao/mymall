package com.zhichao.mall.auth.feign;

import com.zhichao.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Description: 第三方服务的接口
 * @Author: zhichao
 * @Date: 2022/3/28 00:41
 */
@FeignClient("mall-third-party")
public interface ThirdPartFeignService {

    /**
     * 短信发送
     *
     * @param phone
     * @param code
     * @return
     */
    @GetMapping("/sms/sendMsg")
    R sendMsgCode(@RequestParam("phone") String phone, @RequestParam("code") String code);

}
