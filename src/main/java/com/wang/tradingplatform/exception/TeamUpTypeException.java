package com.wang.tradingplatform.exception;

/**
 * 组团分类（type）不是允许的类型时抛出的业务异常。
 */
public class TeamUpTypeException extends BusinessException {

    public TeamUpTypeException(String message) {
        super(message);
    }

}
