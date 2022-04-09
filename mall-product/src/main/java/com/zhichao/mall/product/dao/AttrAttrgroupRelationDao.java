package com.zhichao.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhichao.mall.product.entity.AttrAttrgroupRelationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 17:42:07
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    /**
     * 批量删除
     *
     * @param entities
     */
    void deleteBatchRelation(@Param("entities") List<AttrAttrgroupRelationEntity> entities);

    /**
     * 获取属于同一分组的商品属性
     *
     * @param attrGroupId
     * @return
     */
    Integer getAttrCountInSameAttrGroup(@Param("attrGroupId") Long attrGroupId);
}
