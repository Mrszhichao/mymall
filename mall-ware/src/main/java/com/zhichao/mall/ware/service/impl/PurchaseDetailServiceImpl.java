package com.zhichao.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhichao.mall.common.constant.WareConstant;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.common.utils.Query;
import com.zhichao.mall.ware.dao.PurchaseDetailDao;
import com.zhichao.mall.ware.entity.PurchaseDetailEntity;
import com.zhichao.mall.ware.service.PurchaseDetailService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                new QueryWrapper<PurchaseDetailEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 批量更新采购项的状态
     *
     * @param ids
     */
    @Override
    public void updateDetailByPurchaseId(List<Long> ids) {
        QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();
        wrapper.in("purchase_id", ids);
        PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
        detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
        this.update(detailEntity, wrapper);
    }

}
