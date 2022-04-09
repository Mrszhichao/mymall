package com.zhichao.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.product.entity.AttrEntity;
import com.zhichao.mall.product.vo.AttrRespVo;
import com.zhichao.mall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 17:42:07
 */
public interface AttrService extends IService<AttrEntity> {

    /**
     * 保存属性
     *
     * @param attr
     */
    void saveAttr(AttrVo attr);

    /**
     * 获取属性信息
     *
     * @param params
     * @param catelogId
     * @param
     * @return
     */
    PageUtils queryBaseAttrList(Map<String, Object> params, Long catelogId, String attrType);

    /**
     * 更新属性信息
     *
     * @param attr
     */
    void updateAttr(AttrVo attr);

    /**
     * 获取指定id的属性和其相关联的信息
     *
     * @param attrId
     * @return
     */
    AttrRespVo getAttrInfo(Long attrId);

    /**
     * 获取属性相关联的基本属性
     *
     * @param attrgroupId
     * @return
     */
    List<AttrEntity> getRelationAttr(Long attrgroupId);

    /**
     * 获取未关联的基本属性
     *
     * @param params
     * @param attrgroupId
     * @return
     */
    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

    /**
     * 删除属性和其相关联的表
     *
     * @param attrIds
     */
    void removeAttrByIds(Long[] attrIds);

    /**
     * 获取可以别检索的属性
     *
     * @param attrIds
     * @return
     */
    List<Long> getSearchAttrIdInfo(List<Long> attrIds);
}

