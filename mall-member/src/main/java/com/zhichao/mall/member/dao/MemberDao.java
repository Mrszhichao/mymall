package com.zhichao.mall.member.dao;

import com.zhichao.mall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 18:54:11
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
