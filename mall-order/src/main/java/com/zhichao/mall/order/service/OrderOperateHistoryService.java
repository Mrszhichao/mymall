package com.zhichao.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.order.entity.OrderOperateHistoryEntity;

import java.util.Map;

/**
 * 订单操作历史记录
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 18:51:00
 */
public interface OrderOperateHistoryService extends IService<OrderOperateHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

