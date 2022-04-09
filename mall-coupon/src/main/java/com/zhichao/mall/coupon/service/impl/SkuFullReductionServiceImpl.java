package com.zhichao.mall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhichao.mall.common.to.MemberPrice;
import com.zhichao.mall.common.to.SkuReductionTo;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.common.utils.Query;
import com.zhichao.mall.coupon.dao.SkuFullReductionDao;
import com.zhichao.mall.coupon.entity.MemberPriceEntity;
import com.zhichao.mall.coupon.entity.SkuFullReductionEntity;
import com.zhichao.mall.coupon.entity.SkuLadderEntity;
import com.zhichao.mall.coupon.service.MemberPriceService;
import com.zhichao.mall.coupon.service.SkuFullReductionService;
import com.zhichao.mall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;

    @Autowired
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存sku的优惠满减信息
     *
     * @param skuReductionTo
     */
    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {

        // sms_sku_ladder
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        if (skuReductionTo.getFullCount() > 0) {
            BeanUtils.copyProperties(skuReductionTo, skuLadderEntity);
            skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
            skuLadderService.save(skuLadderEntity);
        }

        // sms_sku_full_reduction
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        if (skuReductionTo.getFullPrice().compareTo(BigDecimal.ZERO) > 0) {
            BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
            this.save(skuFullReductionEntity);
        }

        // sms_member_price
        List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
        if (CollectionUtils.isEmpty(memberPrice)) {
            return;
        }
        List<MemberPriceEntity> memberPriceEntities =
                memberPrice.stream()
                        .filter(item -> item.getPrice().compareTo(BigDecimal.ZERO) > 0)
                        .map(item -> {
                            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                            memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
                            memberPriceEntity.setMemberLevelId(item.getId());
                            memberPriceEntity.setMemberLevelName(item.getName());
                            memberPriceEntity.setMemberPrice(item.getPrice());
                            memberPriceEntity.setAddOther(1);
                            return memberPriceEntity;
                        }).collect(Collectors.toList());
        memberPriceService.saveBatch(memberPriceEntities);
    }

}
