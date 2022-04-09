package com.zhichao.mall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.common.utils.Query;
import com.zhichao.mall.member.dao.MemberDao;
import com.zhichao.mall.member.entity.MemberEntity;
import com.zhichao.mall.member.entity.MemberLevelEntity;
import com.zhichao.mall.member.exception.PhoneExistException;
import com.zhichao.mall.member.exception.UsernameExistException;
import com.zhichao.mall.member.service.MemberLevelService;
import com.zhichao.mall.member.service.MemberService;
import com.zhichao.mall.member.vo.UserRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * 会员
 *
 * @author zhichao
 */
@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 会员注册
     *
     * @param vo
     */
    @Override
    public void register(UserRegisterVo vo) {

        MemberEntity memberEntity = new MemberEntity();
        // 设置默认等级
        MemberLevelEntity memberLevelEntity = memberLevelService.getDefaultLevel();
        memberEntity.setLevelId(memberLevelEntity.getId());

        // 检查用户名和手机号唯一性
        checkPhoneUnique(vo.getPhoneNum());
        checkUsernameUnique(vo.getUserName());
        memberEntity.setMobile(vo.getPhoneNum());
        memberEntity.setUsername(vo.getUserName());

        // 可逆加密和不可逆加密
        // MD5盐值加密
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(vo.getPassword());
        memberEntity.setPassword(encode);
        baseMapper.insert(memberEntity);
    }

    public void checkPhoneUnique(String phone) throws PhoneExistException {
        Integer mobile = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if (mobile > 0) {
            throw new PhoneExistException();
        }
    }

    public void checkUsernameUnique(String username) throws UsernameExistException {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if (count > 0) {
            throw new UsernameExistException();
        }
    }
}
