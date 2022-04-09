package com.zhichao.mall.ware.feign;

import com.zhichao.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description:
 * @Author: zhichao
 * @Date: 2021/7/26 22:22
 */
@FeignClient("mall-product")
public interface ProductFeignService {

    /**
     * 查询sku信息
     *
     * @param skuId
     * @return
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);
}
