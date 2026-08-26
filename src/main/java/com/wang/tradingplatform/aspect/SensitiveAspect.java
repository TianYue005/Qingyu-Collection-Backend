package com.wang.tradingplatform.aspect;


import com.wang.tradingplatform.annotation.Sensitive;
import org.aspectj.lang.annotation.Before;

public class SensitiveAspect {


    //检查用户的评论是否含有敏感词
    @Before("@annotation(Sensitive)")
    public void before(Sensitive sensitive) {
        //评论敏感词 todo
    }
}
