package com.zhichao.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.common.utils.Query;
import com.zhichao.mall.product.dao.AttrGroupDao;
import com.zhichao.mall.product.entity.AttrEntity;
import com.zhichao.mall.product.entity.AttrGroupEntity;
import com.zhichao.mall.product.service.AttrGroupService;
import com.zhichao.mall.product.service.AttrService;
import com.zhichao.mall.product.vo.AttrGroupWithAttrsVo;
import com.zhichao.mall.product.vo.SpuItemSaleAttrVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    /**
     * 属性
     */
    @Autowired
    AttrService attrService;

    /**
     * 根据商品id查询商品的属性分类
     *
     * @param params    分页以及检索key
     * @param catelogId 商品id
     * @return
     */
    @Override
    public PageUtils queryPageByCatelogId(Map<String, Object> params, Long catelogId) {
        IPage<AttrGroupEntity> page = null;
        QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<>();
        if (!catelogId.equals(0L)) {
            // 如果输入了检索关键字，则将检索关键字拼接到条件中
            String key = (String) params.get("key");
            queryWrapper.eq("catelog_id", catelogId);
            if (StringUtils.isNotBlank(key)) {
                queryWrapper.and(param -> {
                    param.eq("attr_group_id", key).or().like("attr_group_name", key);
                });
            }
        }
        page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                queryWrapper);

        return new PageUtils(page);
    }

    /**
     * 根据分类id查出所有的分组以及这些组里面的属性
     *
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {

        // 获取分类的属性分组
        List<AttrGroupEntity> attrGroupEntities = this.list(
                new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        if (CollectionUtils.isEmpty(attrGroupEntities)) {
            return new ArrayList<>();
        }

        // 查询所属性
        List<AttrGroupWithAttrsVo> attrsVoList = attrGroupEntities.stream().map(entity -> {
            AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(entity, attrGroupWithAttrsVo);
            List<AttrEntity> relationAttr = attrService.getRelationAttr(entity.getAttrGroupId());
            attrGroupWithAttrsVo.setAttrs(relationAttr);
            return attrGroupWithAttrsVo;
        }).collect(Collectors.toList());
        return attrsVoList;
    }

    /**
     * 获取商品的规格属性
     *
     * @param spuId
     * @param catalogId
     * @return
     */
    @Override
    public List<SpuItemSaleAttrVo> getSpuItemSaleAttr(Long spuId, Long catalogId) {
        List<SpuItemSaleAttrVo> SpuItemSaleAttr =
                baseMapper.selectSpuItemSaleAttr(spuId, catalogId);
        return SpuItemSaleAttr;
    }
}
