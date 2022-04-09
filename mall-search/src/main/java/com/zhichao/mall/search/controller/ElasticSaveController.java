package com.zhichao.mall.search.controller;

import com.zhichao.mall.common.utils.R;
import com.zhichao.mall.common.utils.ResultCode;
import com.zhichao.mall.search.service.ProductSkuService;
import com.zhichao.mall.search.to.SkuInfoEsModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @Description: es保存操作
 * @Author: zhichao
 * @Date: 2021/11/8 22:39
 */
@Slf4j
@RequestMapping("/elasticsearch/save")
@RestController
public class ElasticSaveController {

    /**
     * 商品相关操作service
     */
    @Autowired
    private ProductSkuService productSkuService;

    /**
     * 商品sku信息保存
     *
     * @param skuInfo
     * @return
     */
    @RequestMapping("/product")
    public R saveProductSpuEsModel(@RequestBody List<SkuInfoEsModel> skuInfo) {
        try {
            productSkuService.saveProductSpuInfo(skuInfo);
            return R.ok();
        } catch (IOException e) {
            log.error("商品上架失败");
            e.printStackTrace();
        }
        return R.error(ResultCode.PRODUCT_UP_EXCEPTION.getCode(), ResultCode.PRODUCT_UP_EXCEPTION.getMessage());
    }
}
