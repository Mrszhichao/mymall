package com.zhichao.mall.coupon.dao;

import com.zhichao.mall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 18:52:55
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
