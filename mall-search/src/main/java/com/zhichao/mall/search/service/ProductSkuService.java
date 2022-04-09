package com.zhichao.mall.search.service;

import com.zhichao.mall.search.to.SkuInfoEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @Description: 商品es相关操作服务层
 * @Author: zhichao
 * @Date: 2021/11/8 22:54
 */
public interface ProductSkuService {

    /**
     * 保存商品sku
     *
     * @param skuInfo
     * @throws IOException
     */
    void saveProductSpuInfo(List<SkuInfoEsModel> skuInfo) throws IOException;
}
