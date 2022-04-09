package com.zhichao.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.member.entity.MemberLevelEntity;

import java.util.Map;

/**
 * 会员等级
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 18:54:11
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取会员等级
     *
     * @return
     */
    MemberLevelEntity getDefaultLevel();
}

