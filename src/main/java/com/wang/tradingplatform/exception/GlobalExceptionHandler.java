package com.wang.tradingplatform.exception;

import com.wang.tradingplatform.pojo.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //Token过期检测
    @ExceptionHandler(TokenException.class)
    public Result<String> tokenExceptionHandler(TokenException e) {
        return Result.error(e.getMessage());
    }

    //业务参数校验不通过（含 TeamUpTypeException 等业务异常）
    @ExceptionHandler(BusinessException.class)
    public Result<String> businessExceptionHandler(BusinessException e) {
        return Result.error(e.getMessage());
    }

    //其它未预期的运行时异常兜底，避免直接返回 500 空信息
    @ExceptionHandler(RuntimeException.class)
    public Result<String> runtimeExceptionHandler(RuntimeException e) {
        log.error("系统运行时异常: {}", e.getMessage(), e);
        return Result.error(e.getMessage() == null ? "系统异常，请稍后重试" : e.getMessage());
    }
}
