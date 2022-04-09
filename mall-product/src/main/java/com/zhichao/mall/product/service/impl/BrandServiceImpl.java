package com.zhichao.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.common.utils.Query;
import com.zhichao.mall.product.dao.BrandDao;
import com.zhichao.mall.product.entity.BrandEntity;
import com.zhichao.mall.product.service.BrandService;
import com.zhichao.mall.product.service.CategoryBrandRelationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: 品牌相关操作
 * @Author: zhichao
 * @Date: 2021/6/27 20:27
 */
@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private CategoryBrandRelationService relationService;

    /**
     * 根据参数查询品牌数据
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // 模糊查询
        String key = (String) params.get("key");
        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<>();

        if (StringUtils.isNotEmpty(key)) {
            wrapper.eq("brand_id", key).or().like("name", key);
        }
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 更新信息以及其关联的信息
     *
     * @param brand
     */
    @Override
    // TODO
    @Transactional(rollbackFor = Exception.class)
    public void updateRelationById(BrandEntity brand) {
        // 更新品牌表
        this.updateById(brand);
        // 更新与品牌表关联的信息
        relationService.updateBrand(brand.getBrandId(), brand.getName());
    }


    /**
     * 获取该分类的可能品牌
     *
     * @param catId
     * @return
     */
    @Override
    public List<BrandEntity> getBrandsByCatelogId(Long catId) {

        if (catId == null) {
            return new ArrayList<>();
        }

        // 查询分类和品牌关联
        List<Long> brandIds = relationService.getBrandIdsByCatelogId(catId);
        if (CollectionUtils.isEmpty(brandIds)) {
            return new ArrayList<>();
        }

        // 查询品牌信息
        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<>();
        wrapper.in("brand_id", brandIds);
        List<BrandEntity> brands = this.list(wrapper);
        return brands;
    }

}
