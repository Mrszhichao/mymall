package com.zhichao.mall.coupon.dao;

import com.zhichao.mall.coupon.entity.MemberPriceEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品会员价格
 * 
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 18:52:55
 */
@Mapper
public interface MemberPriceDao extends BaseMapper<MemberPriceEntity> {
	
}
