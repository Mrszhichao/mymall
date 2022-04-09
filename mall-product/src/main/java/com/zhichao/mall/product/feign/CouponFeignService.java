package com.zhichao.mall.product.feign;

import com.zhichao.mall.common.to.SkuReductionTo;
import com.zhichao.mall.common.to.SpuBoundsTo;
import com.zhichao.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Description: 远程调用优惠券服务
 * @Author: zhichao
 * @Date: 2021/7/25 09:33
 */
@FeignClient("mall-coupon")
public interface CouponFeignService {

    /**
     * 保存积分信息
     *
     * @param spuBoundsTo
     * @return
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundsTo spuBoundsTo);

    /**
     * 保存sku的优惠信息
     *
     * @param skuReductionTo
     * @return
     */
    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
