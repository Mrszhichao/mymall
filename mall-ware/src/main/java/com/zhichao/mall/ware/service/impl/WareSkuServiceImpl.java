package com.zhichao.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhichao.mall.common.to.SkuHasStockTo;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.common.utils.Query;
import com.zhichao.mall.common.utils.R;
import com.zhichao.mall.ware.dao.WareSkuDao;
import com.zhichao.mall.ware.entity.WareSkuEntity;
import com.zhichao.mall.ware.feign.ProductFeignService;
import com.zhichao.mall.ware.service.WareSkuService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 优惠券相关操作
 *
 * @author zhichao
 * @date 2021-05-23 17:42:07
 */
@Service("wareSkuService")
@Transactional(rollbackFor = Exception.class)
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {


    @Autowired
    WareSkuDao wareSkuDao;

    @Autowired
    ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (StringUtils.isNotEmpty(skuId)) {
            wrapper.eq("sku_id", skuId);
        }

        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotEmpty(wareId)) {
            wrapper.eq("ware_id", wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 采购成功后添加商品库存信息
     *
     * @param skuId  采购的商品id
     * @param wareId 仓库id
     * @param skuNum 商品数量
     */
    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        // 判断是否有该商品的库存记录
        List<WareSkuEntity> skuEntityList = baseMapper.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (CollectionUtils.isEmpty(skuEntityList)) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            // 远程调用商品系统，查询商品名称
            R info = productFeignService.info(skuId);
            Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");
            if (info.getCode() == 0) {
                wareSkuEntity.setSkuName((String) data.get("skuName"));
            }
            baseMapper.insert(wareSkuEntity);
        } else {
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }
    }

    /**
     * 查询商品库存
     *
     * @param skuIds
     * @return
     */
    @Override
    public List<SkuHasStockTo> getSkuHasStock(List<Long> skuIds) {

        List<SkuHasStockTo> skuHasStockTos = skuIds.stream().map(skuId -> {
            SkuHasStockTo skuHasStockTo = new SkuHasStockTo();
            skuHasStockTo.setSkuId(skuId);
            Long count = baseMapper.getSkuStock(skuId);
            if (count != null && count > 0) {
                skuHasStockTo.setHasStock(true);
            } else {
                skuHasStockTo.setHasStock(false);
            }
            return skuHasStockTo;
        }).collect(Collectors.toList());
        return skuHasStockTos;
    }

}
