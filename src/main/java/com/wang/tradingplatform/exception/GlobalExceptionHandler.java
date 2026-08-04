package com.wang.tradingplatform.exception;

import com.wang.tradingplatform.pojo.vo.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //Token过期检测，虽然没啥用，但是就是要写
    @ExceptionHandler(TokenException.class)
    public Result<String> businessExceptionHandler(TokenException e) {
        return Result.error(
                e.getMessage()
        );
    }
}
