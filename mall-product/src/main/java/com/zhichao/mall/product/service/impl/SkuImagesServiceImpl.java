package com.zhichao.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.common.utils.Query;
import com.zhichao.mall.product.dao.SkuImagesDao;
import com.zhichao.mall.product.entity.SkuImagesEntity;
import com.zhichao.mall.product.service.SkuImagesService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * sku图片
 *
 * @author zhichao
 */
@Service("skuImagesService")
public class SkuImagesServiceImpl extends ServiceImpl<SkuImagesDao, SkuImagesEntity> implements SkuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuImagesEntity> page = this.page(
                new Query<SkuImagesEntity>().getPage(params),
                new QueryWrapper<SkuImagesEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 通过skuId获取到图片集
     *
     * @param skuId
     * @return
     */
    @Override
    public List<SkuImagesEntity> getImagesBySkuId(Long skuId) {
        QueryWrapper<SkuImagesEntity> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("sku_id", skuId);
        List<SkuImagesEntity> skuImagesEntities = baseMapper.selectList(objectQueryWrapper);
        return skuImagesEntities;
    }

}
