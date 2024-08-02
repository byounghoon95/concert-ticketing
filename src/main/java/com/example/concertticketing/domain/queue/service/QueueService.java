package com.example.concertticketing.domain.queue.service;

import com.example.concertticketing.domain.queue.model.Queue;

import java.time.LocalDateTime;

public interface QueueService {
    Queue enqueue(Long memberId);
    boolean verify(Long memberId);
    void updateActiveTokenToExpired(LocalDateTime time);
    Queue getInfo(Long memberId);
    void updateWaitTokenToActive(LocalDateTime time, int available);
    void expiredToken(Long memberId);
}
