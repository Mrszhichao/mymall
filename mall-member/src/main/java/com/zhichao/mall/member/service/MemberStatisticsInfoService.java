package com.zhichao.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.member.entity.MemberStatisticsInfoEntity;

import java.util.Map;

/**
 * 会员统计信息
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 18:54:10
 */
public interface MemberStatisticsInfoService extends IService<MemberStatisticsInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

