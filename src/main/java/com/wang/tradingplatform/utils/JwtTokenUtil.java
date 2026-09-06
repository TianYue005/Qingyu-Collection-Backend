package com.wang.tradingplatform.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
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
    public String generateToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }


    // 从token中获取用户ID
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token.trim())
                .getBody();
        return claims.getSubject();
    }

    // 校验token是否正确且没过期
    public boolean validateToken(String token, Long userId) {
        String username = getUserIdFromToken(token);
        return username.equals(String.valueOf(userId)) && !isTokenExpired(token);
    }

    // 判断token是否过期
    public boolean isTokenExpired(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            // 获取过期时间
            Date expiration = claimsJws.getBody().getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            // ✅ 令牌过期
            return true;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            // ✅ token非法、篡改、空token
            return true;
        }
    }
}
