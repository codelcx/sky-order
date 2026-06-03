package com.sky.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * JWT 令牌工具类
 */
public class JwtUtil {

    /**
     * 生成 JWT 令牌
     *
     * @param claims    载荷
     * @param secretKey 密钥
     * @param ttl       过期时间（毫秒）
     * @return JWT 令牌字符串
     */
    public static String createJWT(String secretKey, long ttl, Map<String, Object> claims) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ttl))
                .signWith(key)
                .compact();
    }

    /**
     * 解析 JWT 令牌
     *
     * @param token     JWT 令牌
     * @param secretKey 密钥
     * @return 载荷
     */
    public static Claims parseJWT(String secretKey, String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
