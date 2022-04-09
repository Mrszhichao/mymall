package com.zhichao.mall.product.feign;

import com.zhichao.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Description: 远程调用库存系统
 * @Author: zhichao
 * @Date: 2021/11/3 23:04
 */
@FeignClient("mall-ware")
public interface WareFeignService {

    /**
     * 查询商品sku的库存
     *
     * @param skuIds
     * @return
     */
    @PostMapping("/ware/waresku/hasStock")
    R getSkuStock(@RequestBody List<Long> skuIds);
}
