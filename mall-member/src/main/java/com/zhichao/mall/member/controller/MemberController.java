package com.zhichao.mall.member.controller;

import com.zhichao.mall.common.utils.PageUtils;
import com.zhichao.mall.common.utils.R;
import com.zhichao.mall.common.utils.ResultCode;
import com.zhichao.mall.member.entity.MemberEntity;
import com.zhichao.mall.member.exception.PhoneExistException;
import com.zhichao.mall.member.exception.UsernameExistException;
import com.zhichao.mall.member.service.MemberService;
import com.zhichao.mall.member.vo.UserRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 会员
 *
 * @author zhichao
 * @email sunlightcs@gmail.com
 * @date 2021-05-23 18:54:11
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 会员注册
     *
     * @param vo
     * @return
     */
    @PostMapping("/register")
    public R register(@RequestBody UserRegisterVo vo) {
        try {
            memberService.register(vo);
        } catch (PhoneExistException e) {
            return R.error(ResultCode.PHONE_EXIST_EXCEPTION.getCode(),
                    ResultCode.PHONE_EXIST_EXCEPTION.getMessage());
        } catch (UsernameExistException e) {
            return R.error(ResultCode.USERNAME_EXIST_EXCEPTION.getCode(),
                    ResultCode.USERNAME_EXIST_EXCEPTION.getMessage());
        }
        return R.ok();
    }
}
