package com.wang.tradingplatform.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenUtil {
    @Value("${jwt.secret}")
    private String secretStr;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey secretKey;

    //初始化密钥
    @PostConstruct
    void init() {
        /*获取标准Base64解码器对象*/
        Base64.Decoder decoder = Base64.getDecoder();
        /*执行解码*/
        byte[] rawBytes = decoder.decode(secretStr);
        /*生成密钥对象*/
        this.secretKey = Keys.hmacShaKeyFor(rawBytes);
    }

    //根据用户信息生成JWT令牌
    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(userDetails.getUsername()) // 用户名存入subject
                .setIssuedAt(now) // 签发时间
                .setExpiration(expireDate) // 过期时间
                .signWith(secretKey, SignatureAlgorithm.HS256) // 加密算法
                .compact();
    }
}
