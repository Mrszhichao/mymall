package com.zhichao.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.product.entity.SpuInfoEntity;
import com.zhichao.mall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 17:42:06
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存商品
     *
     * @param vo
     */
    void saveSpuInfo(SpuSaveVo vo);

    /**
     * 根据条件查询商品信息
     *
     * @param params
     * @return
     */
    PageUtils queryPageByCondition(Map<String, Object> params);

    /**
     * 商品上架
     *
     * @param spuId
     */
    void upShelf(Long spuId);
}

