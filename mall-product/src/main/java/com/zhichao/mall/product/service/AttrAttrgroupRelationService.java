package com.zhichao.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.product.entity.AttrAttrgroupRelationEntity;
import com.zhichao.mall.product.vo.AttrGroupRelationVo;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 17:42:07
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 批量保存分组关联
     *
     * @param vos
     */
    void saveBatch(List<AttrGroupRelationVo> vos);

    /**
     * 删除属性相关信息
     *
     * @param vos
     */
    void deleteAttrRelation(AttrGroupRelationVo[] vos);
}

