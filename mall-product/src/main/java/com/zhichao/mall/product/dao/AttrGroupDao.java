package com.zhichao.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhichao.mall.product.entity.AttrGroupEntity;
import com.zhichao.mall.product.vo.SpuItemSaleAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性分组
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 17:42:07
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    /**
     * 获取商品属性
     *
     * @param spuId
     * @param catalogId
     * @return
     */
    List<SpuItemSaleAttrVo> selectSpuItemSaleAttr(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}
