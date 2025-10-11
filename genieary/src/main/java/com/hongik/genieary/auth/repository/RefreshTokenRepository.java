package com.hongik.genieary.auth.repository;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;

    // 저장
    public void save(String email, String refreshToken, long expirationMillis) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(email, refreshToken, Duration.ofMillis(expirationMillis));
    }

    // 조회
    public Optional<String> findByEmail(String email) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String token = ops.get(email);
        return Optional.ofNullable(token);
    }

    // 삭제
    public void deleteByEmail(String email) {
        redisTemplate.delete(email);
    }
}