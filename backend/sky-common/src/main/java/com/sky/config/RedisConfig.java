package com.sky.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import java.time.Duration;

/**
 * Redis 配置类，提供 RedisTemplate 和缓存管理器。
 * <p>
 * 基于 Spring Data Redis 4.x（Spring Boot 4.x）实现，
 * 使用 {@link GenericJacksonJsonRedisSerializer}（Jackson 3）进行序列化，
 * 并启用默认类型以支持多态反序列化。
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * 配置 RedisTemplate，使用 Jackson JSON 序列化 value。
     * <p>
     * Key 使用 {@link StringRedisSerializer}，
     * Value / HashValue 使用 {@link GenericJacksonJsonRedisSerializer}（含类型信息）。
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        // 创建 RedisTemplate 实例并设置连接工厂
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 创建多态类型校验器，允许 Object 及其子类的反序列化
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .build();

        // 创建 Jackson 3 JSON 序列化器，启用默认类型以在 JSON 中写入 @class 字段
        GenericJacksonJsonRedisSerializer serializer = GenericJacksonJsonRedisSerializer.builder()
                .enableDefaultTyping(ptv)
                .build();

        // Key 和 HashKey 使用 String 序列化器，Value 和 HashValue 使用 JSON 序列化器（含类型信息）
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 配置 Redis 缓存管理器，支持 {@code @Cacheable}、{@code @CacheEvict} 等注解。
     * <p>
     * 默认 TTL 为 1 小时，序列化策略与 {@link #redisTemplate} 一致。
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 创建多态类型校验器，允许 Object 及其子类的反序列化
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .build();

        // 创建 Jackson 3 JSON 序列化器，启用默认类型以在 JSON 中写入 @class 字段
        GenericJacksonJsonRedisSerializer serializer = GenericJacksonJsonRedisSerializer.builder()
                .enableDefaultTyping(ptv)
                .build();

        // 配置缓存：TTL 1 小时，key 使用 String 序列化，value 使用 JSON 序列化
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}
