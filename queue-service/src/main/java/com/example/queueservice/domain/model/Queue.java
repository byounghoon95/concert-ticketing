package com.example.queueservice.domain.model;

import java.time.LocalDateTime;

public class Queue {
    private Long memberId;

    private LocalDateTime expiredAt;

    private Long position;

    public Long getMemberId() {
        return memberId;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public Long getPosition() {
        return position;
    }

    public Queue(Long memberId, Long position) {
        this.memberId = memberId;
        this.position = position;
    }
}
