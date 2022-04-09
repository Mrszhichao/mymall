package com.zhichao.mall.member.feign;

import com.zhichao.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description: 优惠券的远程调用接口
 * @Author: zhichao
 * @Date: 2021/5/29 19:40
 */
@Component
@FeignClient("mall-coupon")
public interface CouponClient {

    @RequestMapping("/coupon/coupon/test")
    public R test();
}
