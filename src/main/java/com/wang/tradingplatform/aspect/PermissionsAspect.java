package com.wang.tradingplatform.aspect;

import com.wang.tradingplatform.annotation.Permission;
import com.wang.tradingplatform.exception.TokenException;
import com.wang.tradingplatform.utils.RedisUtil;
import com.wang.tradingplatform.utils.UserContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionsAspect {

    private final RedisUtil redisUtil;

    public PermissionsAspect(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }


    @Before("@annotation(permission)")
    public void before(Permission permission) {
        String redisToken = (String) redisUtil.get(String.valueOf(UserContext.getCurrentUserId()));
        String jwtUser = UserContext.getJwtUser();
        if (jwtUser == null || !jwtUser.equals(redisToken)) {
            throw new  TokenException("当前用户的Token已过期");
        }
    }
}
