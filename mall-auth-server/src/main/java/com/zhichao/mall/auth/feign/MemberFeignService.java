package com.zhichao.mall.auth.feign;

import com.zhichao.mall.auth.vo.UserRegisterVo;
import com.zhichao.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Description: 调用会员服务接口
 * @Author: zhichao
 * @Date: 2022/4/5 16:11
 */
@FeignClient("mall-member")
public interface MemberFeignService {

    /**
     * 会员注册
     *
     * @param vo
     * @return
     */
    @PostMapping("/member/member/register")
    R register(@RequestBody UserRegisterVo vo);

}
