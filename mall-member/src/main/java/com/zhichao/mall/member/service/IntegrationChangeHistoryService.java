package com.zhichao.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.member.entity.IntegrationChangeHistoryEntity;

import java.util.Map;

/**
 * 积分变化历史记录
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 18:54:11
 */
public interface IntegrationChangeHistoryService extends IService<IntegrationChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

