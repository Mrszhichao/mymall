package com.zhichao.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhichao.mall.product.entity.AttrEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 17:42:07
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    /**
     * 查询可以被检索的属性
     *
     * @param attrIds
     * @return
     */
    List<Long> selectSearchIds(@Param("attrIds") List<Long> attrIds);
}
