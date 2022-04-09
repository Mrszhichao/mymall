package com.zhichao.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.common.utils.Query;
import com.zhichao.mall.product.dao.SpuImagesDao;
import com.zhichao.mall.product.entity.SpuImagesEntity;
import com.zhichao.mall.product.service.SpuImagesService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("spuImagesService")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesDao, SpuImagesEntity> implements SpuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuImagesEntity> page = this.page(
                new Query<SpuImagesEntity>().getPage(params),
                new QueryWrapper<SpuImagesEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 批量保存spu图片
     *
     * @param images
     * @param spuId
     */
    @Override
    public void saveImages(List<String> images, Long spuId) {
        if (!CollectionUtils.isEmpty(images)) {
            List<SpuImagesEntity> imagesEntities = images.stream().map(image -> {
                SpuImagesEntity imagesEntity = new SpuImagesEntity();
                imagesEntity.setImgUrl(image);
                imagesEntity.setSpuId(spuId);
                return imagesEntity;
            }).collect(Collectors.toList());
            this.saveBatch(imagesEntities);
        }
    }

}
