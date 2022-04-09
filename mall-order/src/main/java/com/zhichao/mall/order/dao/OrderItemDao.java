package com.zhichao.mall.order.dao;

import com.zhichao.mall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 18:51:00
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
