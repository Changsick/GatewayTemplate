package com.song.gateway.support;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<Boolean> isBlacklisted(String token) {
        return Mono.fromCallable(() -> redisTemplate.hasKey(token)).map(Boolean.TRUE::equals);
    }
}
