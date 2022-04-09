package com.zhichao.mall.ware.controller;

import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.common.utils.R;
import com.zhichao.mall.ware.entity.PurchaseEntity;
import com.zhichao.mall.ware.service.PurchaseService;
import com.zhichao.mall.ware.vo.MergePurchaseVo;
import com.zhichao.mall.ware.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 采购信息
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 18:55:45
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    // /ware/purchase/done
    @PostMapping("/done")
    public R donePurchase(@RequestBody PurchaseDoneVo vo) {
        purchaseService.done(vo);
        return R.ok();
    }

    /**
     * 领取采购单
     *
     * @param ids
     * @return
     */
    @PostMapping("/received")
    public R receivedPurchase(@RequestBody List<Long> ids) {
        purchaseService.received(ids);
        return R.ok();
    }

    /**
     * 合并采购单
     *
     * @param vo
     * @return
     */
    @PostMapping("/merge")
    public R merge(@RequestBody MergePurchaseVo vo) {
        purchaseService.mergePurchase(vo);
        return R.ok();
    }


    /**
     * 查询未领取的采购单
     */
    @RequestMapping("/unreceive/list")
    public R unreceivedList(@RequestParam Map<String, Object> params) {
        PageUtils page = purchaseService.queryPageUnrecPurchase(params);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase) {
        purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase) {
        purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
