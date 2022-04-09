package com.zhichao.mall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.zhichao.mall.auth.constant.AuthServerConstant;
import com.zhichao.mall.auth.feign.MemberFeignService;
import com.zhichao.mall.auth.service.RegisterService;
import com.zhichao.mall.auth.vo.UserRegisterVo;
import com.zhichao.mall.common.utils.R;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 注册控制层
 * @Author: zhichao
 * @Date: 2022/4/5 09:18
 */
@Controller
public class RegisterController {

    @Autowired
    RegisterService registerService;

    /**
     * redis
     */
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    MemberFeignService memberFeignService;


    @PostMapping("/register")
    public String register(@Valid UserRegisterVo vo,
                           BindingResult bindingResult, RedirectAttributes attributes) {
        // 参数校验失败
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            // 封装成map
            Map<String, String> errors
                    = fieldErrors.stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            attributes.addFlashAttribute("errors", errors);
            // 从定向到注册页
            return "redirect:http://auth.mymall.com/register.html";
        }
        // 验证验证码是不是正确
        String code = vo.getCode();
        String codeRedisKey = AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhoneNum();
        String result = redisTemplate.opsForValue().get(codeRedisKey);
        if (StringUtils.isNotEmpty(result) && result.equals(code)) {
            redisTemplate.delete(codeRedisKey);
            // 调用会员服务进行保存
            R regResult = memberFeignService.register(vo);
            // 保存失败
            if (regResult.getCode() != 0) {
                Map<String, String> errors = new HashMap<>();
                errors.put("msg", regResult.getData(new TypeReference<String>() {
                }));
                attributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.mymall.com/register.html";
            }

        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            attributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.mymall.com/register.html";
        }

        return "redirect:/login.html";
    }
}
