package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性
 */
@Component
@ConfigurationProperties(prefix = "sky.jwt")
@Data
public class JwtProperties {

    // 管理端员工令牌密钥
    private String adminSecretKey;
    // 管理端令牌过期时间（毫秒）
    private long adminTtl;
    // 管理端令牌请求头名称
    private String adminTokenName;

    // 用户端微信令牌密钥
    private String userSecretKey;
    // 用户端令牌过期时间（毫秒）
    private long userTtl;
    // 用户端令牌请求头名称
    private String userTokenName;
}
