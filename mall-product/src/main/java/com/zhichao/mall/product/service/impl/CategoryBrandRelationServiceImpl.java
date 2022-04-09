package com.zhichao.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhichao.mall.product.dao.CategoryBrandRelationDao;
import com.zhichao.mall.product.entity.BrandEntity;
import com.zhichao.mall.product.entity.CategoryBrandRelationEntity;
import com.zhichao.mall.product.entity.CategoryEntity;
import com.zhichao.mall.product.service.BrandService;
import com.zhichao.mall.product.service.CategoryBrandRelationService;
import com.zhichao.mall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @Description: 属性和属性分组关联
 * @Author: zhichao
 * @Date: 2021/6/27 20:27
 */
@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;


    /**
     * 根据品牌id查询商品种别
     *
     * @param brandId
     * @return
     */
    @Override
    public List<CategoryBrandRelationEntity> getCatelogList(Long brandId) {
        // 查询商品种别
        QueryWrapper<CategoryBrandRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("brand_id", brandId);
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 保存商品关联信息
     *
     * @param categoryBrandRelation
     */
    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();

        // 查询品牌和商品名称
        CategoryEntity categoryEntity = categoryService.getById(catelogId);
        BrandEntity brandEntity = brandService.getById(brandId);

        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());

        this.save(categoryBrandRelation);
    }

    /**
     * 更新品牌相关信息
     *
     * @param brandId
     * @param name
     */
    @Override
    public void updateBrand(Long brandId, String name) {
        CategoryBrandRelationEntity brandRelation = new CategoryBrandRelationEntity();
        brandRelation.setBrandId(brandId);
        brandRelation.setBrandName(name);
        QueryWrapper<CategoryBrandRelationEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("brand_id", brandId);
        this.update(brandRelation, wrapper);
    }

    /**
     * 更新商品信息
     *
     * @param catId
     * @param name
     */
    @Override
    public void updateCategory(Long catId, String name) {
        CategoryBrandRelationEntity brandRelation = new CategoryBrandRelationEntity();
        brandRelation.setBrandId(catId);
        brandRelation.setBrandName(name);
        QueryWrapper<CategoryBrandRelationEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("catelog_id", catId);
        this.update(brandRelation, wrapper);
    }

    /**
     * 根据分类id得到品牌id
     *
     * @param catId
     * @return
     */
    @Override
    public List<Long> getBrandIdsByCatelogId(Long catId) {

        QueryWrapper<CategoryBrandRelationEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("catelog_id", catId);
        List<CategoryBrandRelationEntity> list = this.list(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        List<Long> brandIds = list.stream().map(
                entity -> entity.getBrandId()).collect(Collectors.toList());
        return brandIds;
    }

}
