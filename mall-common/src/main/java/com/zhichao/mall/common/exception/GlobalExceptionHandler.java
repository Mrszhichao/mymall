package com.zhichao.mall.common.exception;

import com.zhichao.mall.common.utils.R;
import com.zhichao.mall.common.utils.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 全局异常处理类
 * @Author: zhichao
 * @Date: 2021/6/19 16:30
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 参数校验异常处理
     *
     * @param e 参数校验异常
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> errors = new HashMap<>(10);
        StringBuffer errorMsg = new StringBuffer(ResultCode.VALID_EXCEPTION.getMessage());
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> {
                // 错误信息
                String defaultMessage = error.getDefaultMessage();
                // 错误的项目名称
                String field = error.getField();
                errors.put(field, defaultMessage);
                errorMsg.append(field + ":" + defaultMessage);
            });
        }
        return R.error(ResultCode.VALID_EXCEPTION.getCode(), errorMsg.toString()).put("data", errors);
    }
}
