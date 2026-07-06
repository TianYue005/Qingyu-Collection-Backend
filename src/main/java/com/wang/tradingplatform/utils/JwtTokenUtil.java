package com.wang.tradingplatform.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    //根据用户信息生成token
    public String generateToken(String userAccount) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(userAccount)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }


    // 从token中获取用户账号
    public String getUserAccountFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // 校验token是否正确且没过期
    public boolean validateToken(String token, String userAccount) {
        String username = getUserAccountFromToken(token);
        return username.equals(userAccount) && !isTokenExpired(token);
    }

    // 判断token是否过期
    private boolean isTokenExpired(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration().before(new Date());
    }
}
