package com.zhichao.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.product.entity.SkuInfoEntity;
import com.zhichao.mall.product.vo.SkuItemVo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 17:42:07
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 根据条件对sku信息进行分页查询
     *
     * @param params
     * @return
     */
    PageUtils queryPageByCondition(Map<String, Object> params);

    /**
     * 获取同一spuId下的sku
     *
     * @param spuId
     * @return
     */
    List<SkuInfoEntity> getSkuInfoBySpuId(Long spuId);

    /**
     * 查询商品详细相关信息
     *
     * @return
     */
    SkuItemVo getSkuDetailInfo(Long skuId) throws ExecutionException, InterruptedException;
}

