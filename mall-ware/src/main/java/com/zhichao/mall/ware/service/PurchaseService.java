package com.zhichao.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.ware.entity.PurchaseEntity;
import com.zhichao.mall.ware.vo.MergePurchaseVo;
import com.zhichao.mall.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 18:55:45
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询未领取的采购单
     *
     * @param params
     * @return
     */
    PageUtils queryPageUnrecPurchase(Map<String, Object> params);

    /**
     * 合并采购单
     *
     * @param vo
     */
    void mergePurchase(MergePurchaseVo vo);

    /**
     * 领取采购单
     *
     * @param ids
     */
    void received(List<Long> ids);

    /**
     * 采购完成相关操作
     *
     * @param vo
     */
    void done(PurchaseDoneVo vo);
}

