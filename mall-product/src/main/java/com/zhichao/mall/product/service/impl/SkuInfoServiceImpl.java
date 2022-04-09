package com.zhichao.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.common.utils.Query;
import com.zhichao.mall.product.dao.SkuInfoDao;
import com.zhichao.mall.product.entity.SkuImagesEntity;
import com.zhichao.mall.product.entity.SkuInfoEntity;
import com.zhichao.mall.product.entity.SpuInfoDescEntity;
import com.zhichao.mall.product.service.*;
import com.zhichao.mall.product.vo.SkuItemSaleAttrVo;
import com.zhichao.mall.product.vo.SkuItemVo;
import com.zhichao.mall.product.vo.SpuItemSaleAttrVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * sku信息
 *
 * @author zhichao
 */
@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SkuImagesService skuImagesService;

    @Autowired
    SpuInfoDescService spuInfoDescService;

    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    /**
     * 注入自定义配置的线程池
     */
    @Autowired
    ThreadPoolExecutor threadPoolExecutor;


    @Override

    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 根据条件对sku信息进行分页查询
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and(w -> {
                w.eq("sku_id", key).or().like("sku_name", key);
            });
        }

        String catelogId = (String) params.get("catelogId");
        if (StringUtils.isNotEmpty(catelogId) && !StringUtils.equals("0", catelogId)) {
            wrapper.eq("catalog_id", catelogId);
        }

        String brandId = (String) params.get("brandId");
        if (StringUtils.isNotEmpty(brandId) && !StringUtils.equals("0", brandId)) {
            wrapper.eq("brand_id", brandId);
        }

        String max = (String) params.get("max");
        if (StringUtils.isNotEmpty(max) && BigDecimal.ZERO.compareTo(new BigDecimal(max)) < 0) {
            wrapper.le("price", max);
        }

        String min = (String) params.get("min");
        if (StringUtils.isNotEmpty(min) && BigDecimal.ZERO.compareTo(new BigDecimal(min)) < 0) {
            wrapper.ge("price", min);
        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 获取同一spuId下的sku
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SkuInfoEntity> getSkuInfoBySpuId(Long spuId) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id", spuId);
        List<SkuInfoEntity> skuInfoEntities = this.list(wrapper);
        return skuInfoEntities;
    }

    /**
     * 查询商品详细相关信息
     *
     * @return
     */
    @Override
    public SkuItemVo getSkuDetailInfo(Long skuId) throws ExecutionException, InterruptedException {

        final SkuItemVo skuItemVo = new SkuItemVo();

        // 异步执行
        CompletableFuture<SkuInfoEntity> skuInfoEntityCompletableFuture = CompletableFuture.supplyAsync(() -> {
            // 1.sku的基本信息
            SkuInfoEntity skuInfoEntity = baseMapper.selectById(skuId);
            skuItemVo.setSkuInfoEntity(skuInfoEntity);
            return skuInfoEntity;
        }, threadPoolExecutor);

        // 异步执行
        CompletableFuture<Void> spuInfoDescFuture = skuInfoEntityCompletableFuture.thenAcceptAsync((res) -> {
            // 3.spu的介绍
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setSpuInfoDescEntity(spuInfoDescEntity);
        }, threadPoolExecutor);

        // 异步执行
        CompletableFuture<Void> saleAttrFuture = skuInfoEntityCompletableFuture.thenAcceptAsync((res) -> {
            // 4.sku的销售属性组合
            List<SkuItemSaleAttrVo> skuItemSaleAttr = skuSaleAttrValueService.getSaleAttrBySpuId(res.getSpuId());
            skuItemVo.setSkuItemSaleAttrVo(skuItemSaleAttr);
        }, threadPoolExecutor);

        // 异步执行
        CompletableFuture<Void> spuItemSaleAttrFuture = skuInfoEntityCompletableFuture.thenAcceptAsync((res) -> {
            // 5.规格参数
            List<SpuItemSaleAttrVo> spuItemSaleAttrVos =
                    attrGroupService.getSpuItemSaleAttr(res.getSpuId(), res.getCatalogId());
            skuItemVo.setSpuItemSaleAttrVos(spuItemSaleAttrVos);
        }, threadPoolExecutor);

        // 异步执行
        CompletableFuture<Void> imagesFuture = CompletableFuture.runAsync(() -> {
            // 2.sku的图片信息
            List<SkuImagesEntity> imagesEntities = skuImagesService.getImagesBySkuId(skuId);
            skuItemVo.setSkuImagesEntities(imagesEntities);
        }, threadPoolExecutor);

        // 等待所有任务都完成
        CompletableFuture.allOf(spuInfoDescFuture, saleAttrFuture,
                spuItemSaleAttrFuture, imagesFuture).get();
        
        return skuItemVo;
    }

}
