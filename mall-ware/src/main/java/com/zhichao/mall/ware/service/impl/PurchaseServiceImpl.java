package com.zhichao.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhichao.mall.common.constant.WareConstant;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.common.utils.Query;
import com.zhichao.mall.ware.dao.PurchaseDao;
import com.zhichao.mall.ware.entity.PurchaseDetailEntity;
import com.zhichao.mall.ware.entity.PurchaseEntity;
import com.zhichao.mall.ware.service.PurchaseDetailService;
import com.zhichao.mall.ware.service.PurchaseService;
import com.zhichao.mall.ware.service.WareSkuService;
import com.zhichao.mall.ware.vo.ItemVo;
import com.zhichao.mall.ware.vo.MergePurchaseVo;
import com.zhichao.mall.ware.vo.PurchaseDoneVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 采购相关操作
 *
 * @author zhichao
 * @date 2021-05-23 17:42:07
 */
@Transactional
@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService purchaseDetailService;

    @Autowired
    WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and(w -> {
                w.eq("purchase_id", key).or().eq("sku_id", key);
            });
        }

        String status = (String) params.get("status");
        if (StringUtils.isNotEmpty(status)) {
            wrapper.eq("status", status);
        }

        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotEmpty(wareId)) {
            wrapper.eq("ware_id", wareId);
        }

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 查询未领取的采购单
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPageUnrecPurchase(Map<String, Object> params) {

        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
                        .eq("status", 0).or().eq("status", 1)
        );

        return new PageUtils(page);
    }

    /**
     * 合并采购单
     *
     * @param vo
     */
    @Override
    public void mergePurchase(MergePurchaseVo vo) {
        Long purchaseId = vo.getPurchaseId();
        // 新建采购单
        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        } else {
            // 判断采购单的状态
            PurchaseEntity purchaseStatusEntity = this.getById(purchaseId);
            Boolean isRightStatus = !Objects.isNull(purchaseStatusEntity)
                    && (WareConstant.PurchaseStatusEnum.CREATED.getCode() != purchaseStatusEntity.getStatus()
                    || WareConstant.PurchaseStatusEnum.ASSIGNED.getCode() != purchaseStatusEntity.getStatus());

            if (isRightStatus) {
                return;
            }
        }

        // 合并采购单
        List<Long> items = vo.getItems();
        final Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> purchaseDetailEntities = items.stream().map(item -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(item);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(purchaseDetailEntities);

        // 更新时间
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(finalPurchaseId);
        this.updateById(purchaseEntity);
    }

    /**
     * 领取采购单
     *
     * @param ids
     */
    @Override
    public void received(List<Long> ids) {
        // 1.确认当前采购单已经是新建或者已经分配状态
        List<PurchaseEntity> purchaseEntities = ids.stream().map(id -> {
            PurchaseEntity entity = this.getById(id);
            return entity;
        }).filter(item -> {
            if (item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                    item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {
                return true;
            }
            return false;
        }).map(item -> {
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            return item;
        }).collect(Collectors.toList());

        // 2.改变采购单位状态
        this.updateBatchById(purchaseEntities);

        // 3.改变采购项的状态
        purchaseDetailService.updateDetailByPurchaseId(ids);
    }

    /**
     * 采购完成相关操作
     *
     * @param vo
     */
    @Override
    public void done(PurchaseDoneVo vo) {
        // 1.改变采购项的状态
        List<ItemVo> items = vo.getItems();
        ArrayList<PurchaseDetailEntity> updateEntities = new ArrayList<>();
        Boolean flag = true;
        for (ItemVo item : items) {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
            purchaseDetailEntity.setId(item.getItemId());
            if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()) {
                flag = false;
                purchaseDetailEntity.setStatus(item.getStatus());
            } else {
                // 将采购成功的进行入库
                // 查出采购项的全部信息
                PurchaseDetailEntity detailEntity = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(detailEntity.getSkuId(), detailEntity.getWareId(), detailEntity.getSkuNum());
            }
            updateEntities.add(purchaseDetailEntity);
        }
        purchaseDetailService.updateBatchById(updateEntities);

        // 1.改变采购但的状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(vo.getId());
        Integer status = flag ? WareConstant.PurchaseStatusEnum.HASERROR.getCode() : WareConstant.PurchaseStatusEnum.FINISH.getCode();
        purchaseEntity.setStatus(status);
        this.updateById(purchaseEntity);
    }

}
