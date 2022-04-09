package com.zhichao.mall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhichao.mall.common.constant.ProductConstant;
import com.zhichao.mall.common.to.SkuHasStockTo;
import com.zhichao.mall.common.to.SkuReductionTo;
import com.zhichao.mall.common.to.SpuBoundsTo;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.common.utils.Query;
import com.zhichao.mall.common.utils.R;
import com.zhichao.mall.product.dao.SpuInfoDao;
import com.zhichao.mall.product.entity.*;
import com.zhichao.mall.product.feign.CouponFeignService;
import com.zhichao.mall.product.feign.ElasticSearchFeignService;
import com.zhichao.mall.product.feign.WareFeignService;
import com.zhichao.mall.product.service.*;
import com.zhichao.mall.product.to.SkuInfoEsModel;
import com.zhichao.mall.product.vo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品相关
 *
 * @author zhichao
 */
@Transactional(rollbackFor = Exception.class)
@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService atrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private ElasticSearchFeignService esFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存商品
     *
     * @param vo
     */
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {

        // 1.保存spu的基本信息 pm_spu_info
        Long spuId = this.saveBaseSpuInfo(vo);

        // 2.保存spu的描述图片 pm_spu_info_desc
        this.saveBaseSpuInfoDesc(vo, spuId);

        // 3.保存spu的图片集 pms_spu_images
        List<String> images = vo.getImages();
        spuImagesService.saveImages(images, spuId);

        // 4.保存spu的规格参数 pms_product_attr_value
        this.saveProductAttrValue(vo, spuId);

        // 保存spu的积分信息sms_spu_bounds
        log.debug("开始调用SPU积分系统远程");
        R boundResult = saveSpuBounds(vo, spuId);
        if (boundResult.getCode() != 0) {
            // TODO 添加远程服务调用的全局异常处理exception
            log.error("SPU积分系统远程调用失败");
        }


        // 5.保存当前spu对应的sku信息
        List<Skus> skus = vo.getSkus();
        if (!CollectionUtils.isEmpty(skus)) {
            skus.forEach(item -> {
                // sku信息保存
                SkuInfoEntity skuInfoEntity = this.saveSkuInfo(item, vo, spuId);
                Long skuId = skuInfoEntity.getSkuId();

                // 保存sku图片信息，pms_sku_image
                this.saveSkuImages(item, skuId);

                // 保存sku的销售属性信息，pms_sku_sale_attr_value
                this.saveSkuSaleAttrValue(item, skuId);

                // sku的优惠、满减信息 sms_sku_ladder sms_sku_full_reduction
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                if (item.getFullCount() > 0 || BigDecimal.ZERO.compareTo(item.getFullPrice()) < 0) {
                    BeanUtils.copyProperties(item, skuReductionTo);
                    skuReductionTo.setSkuId(skuId);
                    R r = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r.getCode() != 0) {
                        log.error("远程调用失败");
                    }
                }
            });
        }

    }

    /**
     * 根据条件查询商品信息
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and(w -> {
                w.eq("id", key).or().like("spu_name", key);
            });
        }

        String status = (String) params.get("status");
        if (StringUtils.isNotEmpty(status)) {
            wrapper.eq("publish_status", status);
        }

        String brandId = (String) params.get("brandId");
        if (StringUtils.isNotEmpty(brandId)) {
            wrapper.eq("brand_id", brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if (StringUtils.isNotEmpty(catelogId)) {
            wrapper.eq("catelog_id", catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 商品上架
     *
     * @param spuId
     */
    @Override
    public void upShelf(Long spuId) {
        List<SkuInfoEsModel> skuInfoEsModels = new ArrayList<>();
        // 1.查询spuId下的所有sku信息
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkuInfoBySpuId(spuId);
        // 2.查询spu对应的属性信息
        List<ProductAttrValueEntity> productAttrValueEntities = atrValueService.baseAttrInfoForSpu(spuId);
        List<Long> attrIds = productAttrValueEntities.stream().map(item -> item.getAttrId()).collect(Collectors.toList());
        // 查询符合条件的属性信息
        List<Long> searchAttrId = attrService.getSearchAttrIdInfo(attrIds);
        HashSet<Long> attrIdSet = new HashSet<>(searchAttrId);
        List<SkuInfoEsModel.Attrs> attrValues = productAttrValueEntities.stream().
                filter(item -> attrIdSet.contains(item.getAttrId())).
                map(item -> {
                    SkuInfoEsModel.Attrs attrs = new SkuInfoEsModel.Attrs();
                    attrs.setAttrId(item.getAttrId());
                    attrs.setAttrValue(item.getAttrValue());
                    attrs.setAttrName(item.getAttrName());
                    return attrs;
                }).collect(Collectors.toList());

        // 查询当前sku是否还有库存
        List<Long> skuIds = skuInfoEntities.stream().map(
                sku -> sku.getSkuId()).collect(Collectors.toList());
        Map<Long, Boolean> stock = new HashMap<>();
        try {
            R skuStock = wareFeignService.getSkuStock(skuIds);
            TypeReference<List<SkuHasStockTo>> listTypeReference = new TypeReference<List<SkuHasStockTo>>() {
            };
            List<SkuHasStockTo> skuStockData = skuStock.getData(listTypeReference);
            stock = skuStockData.stream().collect(
                    Collectors.toMap(SkuHasStockTo::getSkuId, SkuHasStockTo::getHasStock));
        } catch (Exception e) {
            // TODO 库存系统调用失败
        }

        // 3.封装信息
        final Map<Long, Boolean> finalStock = stock;
        skuInfoEsModels = skuInfoEntities.stream().map(sku -> {
            SkuInfoEsModel skuInfoEsModel = new SkuInfoEsModel();
            BeanUtils.copyProperties(sku, skuInfoEsModel);
            skuInfoEsModel.setSkuPrice(sku.getPrice());
            skuInfoEsModel.setSkuImg(sku.getSkuDefaultImg());
            // 查询品牌信息
            BrandEntity brandInfo = brandService.getById(sku.getBrandId());
            skuInfoEsModel.setBrandName(brandInfo.getName());
            skuInfoEsModel.setBrandImg(brandInfo.getLogo());
            // 查询分类名称
            CategoryEntity categoryInfo = categoryService.getById(sku.getCatalogId());
            skuInfoEsModel.setCatalogName(categoryInfo.getName());
            // 属性
            skuInfoEsModel.setAttrs(attrValues);
            // 库存
            Boolean hasStock = finalStock.getOrDefault(sku.getSkuId(), false);
            skuInfoEsModel.setHasStock(hasStock);
            skuInfoEsModel.setHotScore(0L);
            return skuInfoEsModel;
        }).collect(Collectors.toList());

        // 4.调用es保存数据
        R esResult = esFeignService.saveProductSpuEsModel(skuInfoEsModels);
        if (esResult.getCode() == 0) {
            // 改变商品spu的状态
            SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
            spuInfoEntity.setId(spuId);
            spuInfoEntity.setPublishStatus(ProductConstant.StatusEnum.SPU_UP.getCode());
            baseMapper.updateById(spuInfoEntity);
        } else {
            // TODO 远程调用失败
        }
    }

    /**
     * 保存spu的基本信息
     *
     * @param vo
     * @return
     */
    private Long saveBaseSpuInfo(SpuSaveVo vo) {
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        baseMapper.insert(spuInfoEntity);
        return spuInfoEntity.getId();
    }

    /**
     * 保存spu的介绍信息图片
     *
     * @param vo
     * @param spuId
     */
    private void saveBaseSpuInfoDesc(SpuSaveVo vo, Long spuId) {
        List<String> descriptors = vo.getDecript();
        SpuInfoDescEntity infoDescEntity = new SpuInfoDescEntity();
        infoDescEntity.setSpuId(spuId);
        infoDescEntity.setDecript(String.join(",", descriptors));
        spuInfoDescService.getBaseMapper().insert(infoDescEntity);
    }


    /**
     * 保存spu的规格参数
     *
     * @param vo
     * @param spuId
     */
    private void saveProductAttrValue(SpuSaveVo vo, Long spuId) {
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> valueEntities = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setSpuId(spuId);
            valueEntity.setAttrId(attr.getAttrId());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            AttrEntity id = attrService.getById(attr.getAttrId());
            valueEntity.setAttrName(id.getAttrName());
            return valueEntity;
        }).collect(Collectors.toList());
        atrValueService.saveBatch(valueEntities);
    }

    /**
     * 调用积分信息服务保存spu的积分信息
     *
     * @param vo
     * @param spuId
     * @return
     */
    private R saveSpuBounds(SpuSaveVo vo, Long spuId) {
        Bounds bounds = vo.getBounds();
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(bounds, spuBoundsTo);
        spuBoundsTo.setSpuId(spuId);
        return couponFeignService.saveSpuBounds(spuBoundsTo);
    }

    /**
     * 保存sku详细信息
     *
     * @param item
     * @param vo
     * @param spuId
     * @return
     */
    private SkuInfoEntity saveSkuInfo(Skus item, SpuSaveVo vo, Long spuId) {
        String defaultImage = StringUtils.EMPTY;
        for (Images image : item.getImages()) {
            if (image.getDefaultImg() == 1) {
                defaultImage = image.getImgUrl();
            }
        }

        SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
        BeanUtils.copyProperties(item, skuInfoEntity);
        skuInfoEntity.setBrandId(vo.getBrandId());
        skuInfoEntity.setCatalogId(vo.getCatalogId());
        skuInfoEntity.setSaleCount(0L);
        skuInfoEntity.setSpuId(spuId);
        skuInfoEntity.setSkuDefaultImg(defaultImage);
        skuInfoService.getBaseMapper().insert(skuInfoEntity);

        return skuInfoEntity;
    }

    /**
     * 保存sku图片集
     *
     * @param item
     * @param skuId
     */
    private void saveSkuImages(Skus item, Long skuId) {
        List<SkuImagesEntity> skuImagesEntities =
                item.getImages().stream()
                        .filter(img -> StringUtils.isNotEmpty(img.getImgUrl()))
                        .map(img -> {
                            SkuImagesEntity imagesEntity = new SkuImagesEntity();
                            imagesEntity.setSkuId(skuId);
                            imagesEntity.setImgUrl(img.getImgUrl());
                            imagesEntity.setDefaultImg(img.getDefaultImg());
                            return imagesEntity;
                        }).collect(Collectors.toList());
        skuImagesService.saveBatch(skuImagesEntities);
    }

    /**
     * 保存sku的销售属性信息
     *
     * @param item
     * @param skuId
     */
    private void saveSkuSaleAttrValue(Skus item, Long skuId) {
        List<Attr> attrs = item.getAttr();
        List<SkuSaleAttrValueEntity> saleAttrValueEntities = attrs.stream().map(attr -> {
            SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
            BeanUtils.copyProperties(attr, attrValueEntity);
            attrValueEntity.setSkuId(skuId);
            return attrValueEntity;
        }).collect(Collectors.toList());
        skuSaleAttrValueService.saveBatch(saleAttrValueEntities);
    }
}
