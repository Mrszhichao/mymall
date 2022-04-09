package com.zhichao.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.product.entity.AttrGroupEntity;
import com.zhichao.mall.product.vo.AttrGroupWithAttrsVo;
import com.zhichao.mall.product.vo.SpuItemSaleAttrVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 17:42:07
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    /**
     * 根据商品id查询商品的属性分类
     *
     * @param params
     * @param catelogId 商品id
     * @return
     */
    PageUtils queryPageByCatelogId(Map<String, Object> params, Long catelogId);

    /**
     * 根据分类id查出所有的分组以及这些组里面的属性
     *
     * @param catelogId
     * @return
     */
    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId);

    /**
     * 获取商品的规格属性
     *
     * @param spuId
     * @param catalogId
     * @return
     */
    List<SpuItemSaleAttrVo> getSpuItemSaleAttr(Long spuId, Long catalogId);
}

