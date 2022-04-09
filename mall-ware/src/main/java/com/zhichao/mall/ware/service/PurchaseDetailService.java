package com.zhichao.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.ware.entity.PurchaseDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 18:55:45
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 批量更新采购项的状态
     *
     * @param ids
     */
    void updateDetailByPurchaseId(List<Long> ids);
}

