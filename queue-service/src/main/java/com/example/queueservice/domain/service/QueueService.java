package com.example.queueservice.domain.service;

import com.example.queueservice.domain.model.Queue;

import java.time.LocalDateTime;

public interface QueueService {
    Queue enqueue(Long memberId);
    boolean verify(Long memberId);
    void expireActiveTokens(LocalDateTime time);
    Queue getInfo(Long memberId);
    void updateWaitTokenToActive(LocalDateTime time, int available);
    void expireActiveToken(Long memberId);
}
