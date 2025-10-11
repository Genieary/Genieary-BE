package com.hongik.genieary.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class JwtBlacklistRepository {

    private final RedisTemplate<String, String> redisTemplate;

    // 블랙리스트에 토큰 저장 (만료시간은 accessToken 남은 시간과 동일하게)
    public void save(String accessToken, long expirationMillis) {
        redisTemplate.opsForValue().set(accessToken, "blacklisted", Duration.ofMillis(expirationMillis));
    }

    // 블랙리스트에 해당 토큰이 있는지 확인
    public boolean exists(String accessToken) {
        return redisTemplate.hasKey(accessToken);
    }
}