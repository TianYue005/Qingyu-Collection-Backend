package com.wang.tradingplatform.exception;

/**
 * 业务参数异常。
 * <p>
 * 当 controller / service 层收到的参数不符合要求，或业务规则校验不通过时抛出，
 * message 中描述具体的失败原因，由 {@link GlobalExceptionHandler} 统一捕获并返回给前端。
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
