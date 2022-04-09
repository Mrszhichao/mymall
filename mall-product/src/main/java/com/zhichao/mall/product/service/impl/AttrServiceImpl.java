package com.zhichao.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhichao.mall.common.constant.ProductConstant;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.common.utils.Query;
import com.zhichao.mall.product.dao.AttrAttrgroupRelationDao;
import com.zhichao.mall.product.dao.AttrDao;
import com.zhichao.mall.product.dao.AttrGroupDao;
import com.zhichao.mall.product.dao.CategoryDao;
import com.zhichao.mall.product.entity.AttrAttrgroupRelationEntity;
import com.zhichao.mall.product.entity.AttrEntity;
import com.zhichao.mall.product.entity.AttrGroupEntity;
import com.zhichao.mall.product.entity.CategoryEntity;
import com.zhichao.mall.product.service.AttrService;
import com.zhichao.mall.product.service.CategoryService;
import com.zhichao.mall.product.vo.AttrRespVo;
import com.zhichao.mall.product.vo.AttrVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品属性相关操作
 *
 * @author zhichao
 * @date 2021-05-23 17:42:07
 */
@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    private final static String ATTR_BASE = "base";
    private final static String ATTR_SALE = "sale";

    @Autowired
    private AttrAttrgroupRelationDao relationDao;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    CategoryService categoryService;

    /**
     * 保存属性表以及属性和属性分组表
     *
     * @param attr
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAttr(AttrVo attr) {
        // 保存属性表
        AttrEntity entity = new AttrEntity();
        BeanUtils.copyProperties(attr, entity);
        boolean saveResult = this.save(entity);
        if (!saveResult) {
            // throw db fail exception
            return;
        }
        // 保存属性和属性分组表
        Long attrGroupId = attr.getAttrGroupId();
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()
                && attrGroupId != null) {
            // 获取属性组内排序
            Integer count = relationDao.getAttrCountInSameAttrGroup(attrGroupId);
            // 保存关联关系
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(entity.getAttrId());
            relationEntity.setAttrGroupId(attrGroupId);
            relationEntity.setAttrSort(count + 1);
            relationDao.insert(relationEntity);
        }
    }

    /**
     * 获取属性及其关联信息
     *
     * @param params
     * @param catelogId
     * @param attrType
     * @return
     */
    @Override
    public PageUtils queryBaseAttrList(Map<String, Object> params, Long catelogId, String attrType) {

        // 根据是否是销售属性还是商品属性
        int attrTypeVal = ATTR_BASE.equalsIgnoreCase(attrType) ?
                ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() :
                ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode();
        QueryWrapper<AttrEntity> queryWrapper =
                new QueryWrapper<AttrEntity>().eq("attr_type", attrTypeVal);

        // 拼接检索条件
        if (catelogId != 0) {
            queryWrapper.eq("catelog_id", catelogId);
        }

        // 获取检索key中的内容
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            //attr_id  attr_name
            queryWrapper.and((wrapper) -> {
                wrapper.eq("attr_id", key).or().like("attr_name", key);
            });
        }

        // 查询属性信息
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();

        // 查询属性关联的信息
        // 查询所属分类名字和所属分组名字
        List<AttrRespVo> respVos = records.stream().map((attrEntity) -> {

            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);

            if (ATTR_BASE.equalsIgnoreCase(attrType)) {
                //1、设置分类和分组的名字
                // 一条属性对应着一个属性和属性分组表
                AttrAttrgroupRelationEntity attrId = relationDao.selectOne(
                        new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
                if (!Objects.isNull(attrId) && !Objects.isNull(attrId.getAttrGroupId())) {
                    // 查询分组名称
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrId.getAttrGroupId());
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }

            // 查询所属分类名
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (!Objects.isNull(categoryEntity)) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }
            return attrRespVo;
        }).collect(Collectors.toList());
        pageUtils.setList(respVos);
        return pageUtils;
    }

    /**
     * 获取指定id的属性和其相关联的信息
     *
     * @param attrId
     * @return
     */
    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo respVo = new AttrRespVo();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, respVo);

        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            //1、设置分组信息
            AttrAttrgroupRelationEntity attrgroupRelation = relationDao.selectOne(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if (attrgroupRelation != null) {
                respVo.setAttrGroupId(attrgroupRelation.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupRelation.getAttrGroupId());
                if (attrGroupEntity != null) {
                    respVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }

        //2、设置分类信息
        Long catelogId = attrEntity.getCatelogId();
        Long[] catelogPath = categoryService.getCateGoryPath(catelogId);
        respVo.setCatelogPath(catelogPath);

        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if (categoryEntity != null) {
            respVo.setCatelogName(categoryEntity.getName());
        }

        return respVo;
    }

    /**
     * 获取属性相关联的基本属性
     *
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {

        List<AttrAttrgroupRelationEntity> relationEntities = relationDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq(
                        "attr_group_id", attrgroupId));
        if (CollectionUtils.isEmpty(relationEntities)) {
            return new ArrayList<>();
        }

        // 获取已经关联的属性id
        List<Long> attrIds = relationEntities.stream().map(
                atttr -> atttr.getAttrId()).collect(Collectors.toList());
        // 查询关联的属性信息
        return listByIds(attrIds);
    }

    /**
     * 获取属性分组未关联的属性信息<br/>
     * 步骤：
     * 1.因为同意商品分类下的不同的属性分组是唯一的，所以先取到该属性分组所对应的商品分类id。
     * 2.根据商品分类id获取到旗下所有的属性分组id。
     * 3.根据这些分组id获取到其关联的所有属性。
     * 4.将存在的属性剔除掉上述 3中的已经关联的属性，就得到还未被关联的属性
     *
     * @param params
     * @param attrgroupId 属性分组id
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {

        // 获取当前分组所对应的分类id
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();

        // 获取当前分类下的所有分组id
        List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(
                new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        final List<Long> attrGroupIds = attrGroupEntities.stream().map(
                item -> item.getAttrGroupId()).collect(Collectors.toList());

        // 获取已经被关联的属性id
        List<AttrAttrgroupRelationEntity> relationEntities = relationDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrGroupIds));
        List<Long> relationAttrIds = relationEntities.stream().map(
                item -> item.getAttrId()).collect(Collectors.toList());

        // 剔除掉已经关联的属性信息
        QueryWrapper<AttrEntity> wrapper =
                new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId)
                        .eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        String key = (String) params.get("key");
        if (!CollectionUtils.isEmpty(relationAttrIds)) {
            wrapper.notIn("attr_id", relationAttrIds);
        }
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and(w -> {
                w.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

    /**
     * 删除属性和其相关联的表
     *
     * @param attrIds
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeAttrByIds(Long[] attrIds) {
        // 删除属性表
        final int deleteCount = baseMapper.deleteBatchIds(Arrays.asList(attrIds));

        // 删除属性关联表
        if (deleteCount != 0) {
            QueryWrapper<AttrAttrgroupRelationEntity> wrapper = new QueryWrapper<>();
            wrapper.in("attr_id", attrIds);
            relationDao.delete(wrapper);
        }
    }

    /**
     * 获取可以别检索的属性
     *
     * @param attrIds
     * @return
     */
    @Override
    public List<Long> getSearchAttrIdInfo(List<Long> attrIds) {
        List<Long> searchIds = baseMapper.selectSearchIds(attrIds);
        return searchIds;
    }

    /**
     * 更新属性相关信息
     *
     * @param attr
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.updateById(attrEntity);

        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            //1、修改分组关联
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            // 获取属性组内排序
            Long attrGroupId = attr.getAttrGroupId();
            Integer attrSort = relationDao.getAttrCountInSameAttrGroup(attrGroupId);
            relationEntity.setAttrGroupId(attrGroupId);
            relationEntity.setAttrId(attr.getAttrId());
            relationEntity.setAttrSort(attrSort);

            Integer count = relationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            if (count > 0) {
                relationDao.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            } else {
                relationDao.insert(relationEntity);
            }
        }
    }
}
