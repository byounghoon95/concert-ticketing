package com.example.concertticketing.domain.queue.service;

import com.example.concertticketing.domain.queue.model.Queue;
import com.example.concertticketing.domain.queue.model.QueueStatus;

import java.time.LocalDateTime;

public interface QueueService {
    Queue enqueue(Long memberId);
    boolean verify(Long memberId);
    void updateActiveTokenToExpired(LocalDateTime time, QueueStatus prev, QueueStatus change);
    Queue getInfo(Long memberId);
    void updateWaitTokenToActive(LocalDateTime now, QueueStatus queueStatus, QueueStatus queueStatus1);

    void expiredToken(Long aLong, QueueStatus queueStatus);
}
