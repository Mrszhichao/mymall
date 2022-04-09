package com.zhichao.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.common.utils.Query;
import com.zhichao.mall.product.dao.AttrAttrgroupRelationDao;
import com.zhichao.mall.product.entity.AttrAttrgroupRelationEntity;
import com.zhichao.mall.product.service.AttrAttrgroupRelationService;
import com.zhichao.mall.product.vo.AttrGroupRelationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 批量保存分组关联
     *
     * @param vos
     */
    @Override
    public void saveBatch(List<AttrGroupRelationVo> vos) {
        HashMap<Long, Integer> attrSortMap = new HashMap<>();
        List<AttrAttrgroupRelationEntity> relationEntities = vos.stream().map(item -> {
            // 获取当前属性分组id所对应的最大AttrSort
            Long attrGroupId = item.getAttrGroupId();
            int attrSort = baseMapper.getAttrCountInSameAttrGroup(attrGroupId);
            Integer newAttrSortVal = attrSortMap.getOrDefault(attrGroupId, attrSort);
            // 准备数据
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attrGroupId);
            relationEntity.setAttrId(item.getAttrId());
            relationEntity.setAttrSort(++newAttrSortVal);
            // 更新前属性分组id所对应的最大AttrSort
            attrSortMap.put(attrGroupId, newAttrSortVal);
            return relationEntity;
        }).collect(Collectors.toList());
        this.saveBatch(relationEntities);
    }

    /**
     * 删除属性相关信息
     *
     * @param vos
     */
    @Override
    public void deleteAttrRelation(AttrGroupRelationVo[] vos) {
        // 转换参数类型
        List<AttrAttrgroupRelationEntity> entities = Arrays.asList(vos).stream().map(item -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        baseMapper.deleteBatchRelation(entities);
    }

}
