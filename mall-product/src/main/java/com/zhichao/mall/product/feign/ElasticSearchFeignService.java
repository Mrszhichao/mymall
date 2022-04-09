package com.zhichao.mall.product.feign;

import com.zhichao.mall.common.utils.R;
import com.zhichao.mall.product.to.SkuInfoEsModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Description: es远程调用
 * @Author: zhichao
 * @Date: 2021/11/8 23:52
 */
@FeignClient("mall-search")
public interface ElasticSearchFeignService {

    /**
     * 商品sku信息保存
     *
     * @param skuInfo
     * @return
     */
    @RequestMapping("/elasticsearch/save/product")
    R saveProductSpuEsModel(@RequestBody List<SkuInfoEsModel> skuInfo);
}
