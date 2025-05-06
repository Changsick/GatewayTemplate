package com.song.auth.webService;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blacklistToken(String token, long ttl) {
        redisTemplate.opsForValue().set(token, "blacklisted", Duration.ofSeconds(ttl));
    }

    public void removeFromBlacklist(String token) {
        redisTemplate.delete(token);
    }
}
