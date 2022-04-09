package com.zhichao.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.member.entity.MemberEntity;
import com.zhichao.mall.member.vo.UserRegisterVo;

import java.util.Map;

/**
 * 会员
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 18:54:11
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 会员注册
     *
     * @param vo
     */
    void register(UserRegisterVo vo);
}

