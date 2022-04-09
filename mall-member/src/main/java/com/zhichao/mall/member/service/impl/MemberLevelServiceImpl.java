package com.zhichao.mall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.common.utils.Query;
import com.zhichao.mall.member.dao.MemberLevelDao;
import com.zhichao.mall.member.entity.MemberLevelEntity;
import com.zhichao.mall.member.service.MemberLevelService;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * 会员等级
 *
 * @author zhichao
 */
@Service("memberLevelService")
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelDao, MemberLevelEntity> implements MemberLevelService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberLevelEntity> page = this.page(
                new Query<MemberLevelEntity>().getPage(params),
                new QueryWrapper<MemberLevelEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 获取会员等级
     *
     * @return
     */
    @Override
    public MemberLevelEntity getDefaultLevel() {
        QueryWrapper<MemberLevelEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("default_status", 1);
        return baseMapper.selectOne(wrapper);
    }

}
