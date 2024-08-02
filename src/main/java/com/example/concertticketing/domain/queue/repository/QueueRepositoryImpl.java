package com.example.concertticketing.domain.queue.repository;

import com.example.concertticketing.domain.queue.model.ActiveQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class QueueRepositoryImpl implements QueueRepository {

    private final RedisTemplate redisTemplate;
    private static final String WAITING_TOKENS_KEY = "waitingTokens";
    private static final String ACTIVE_TOKENS_KEY = "activeTokens";

    @Override
    public void addWaitingQueue(Long memberId) {
        LocalDateTime now = LocalDateTime.now();
       redisTemplate.opsForZSet().add(WAITING_TOKENS_KEY, memberId.toString(), now.toEpochSecond(ZoneOffset.UTC));
    }

    @Override
    public Set<String> getWaitTokens(int count) {
        return redisTemplate.opsForZSet().range(WAITING_TOKENS_KEY, 0, count);
    }

    @Override
    public boolean isInWaitingTokens(Long memberId) {
        return redisTemplate.opsForZSet().score(WAITING_TOKENS_KEY, String.valueOf(memberId)) != null;
    }

    @Override
    public Long getPosition(Long memberId) {
        Long rank = redisTemplate.opsForZSet().rank(WAITING_TOKENS_KEY, memberId.toString());
        return rank != null ? rank + 1 : -1;
    }

    @Override
    public void removeWaitQueue(String memberId) {
        redisTemplate.opsForZSet().remove(ACTIVE_TOKENS_KEY, memberId);
    }

    @Override
    public void removeWaitQueues(Set<String> members) {
        redisTemplate.opsForZSet().remove(WAITING_TOKENS_KEY, members.toArray());
    }

    @Override
    public void addActiveQueues(Set<String> members) {
        redisTemplate.opsForSet().add(ACTIVE_TOKENS_KEY, members.toArray());
    }

    @Override
    public Set<ActiveQueue> getActiveTokens() {
        return (Set<ActiveQueue>) redisTemplate.opsForSet().members(ACTIVE_TOKENS_KEY).stream()
                .map(value -> {
                    String val = value.toString();
                    String[] split = val.split(":");
                    return new ActiveQueue(split[0], split[1]);
                })
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isInActiveTokens(String value) {
        return redisTemplate.opsForSet().isMember(ACTIVE_TOKENS_KEY, value);
    }

    @Override
    public void removeActiveQueue(String value) {
        redisTemplate.opsForSet().remove(ACTIVE_TOKENS_KEY, value);
    }

    @Override
    public void flushAll() {
        RedisConnection redisConnection = this.redisTemplate.getConnectionFactory().getConnection();
        RedisSerializer<String> redisSerializer = this.redisTemplate.getKeySerializer();
        DefaultStringRedisConnection defaultStringRedisConnection = new DefaultStringRedisConnection(redisConnection, redisSerializer);
        defaultStringRedisConnection.flushAll();
    }
}
