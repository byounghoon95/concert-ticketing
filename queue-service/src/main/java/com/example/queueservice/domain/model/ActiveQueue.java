package com.example.queueservice.domain.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class ActiveQueue {
    private String memberId;

    private String expiredAt;

    public String getMemberId() {
        return memberId;
    }

    public String getExpiredAt() {
        return expiredAt;
    }

    public ActiveQueue(String memberId, String expiredAt) {
        this.memberId = memberId;
        this.expiredAt = expiredAt;
    }

    public boolean isExpired(LocalDateTime time) {
        LocalDateTime expiredDateTime = LocalDateTime.ofEpochSecond(Long.parseLong(this.expiredAt), 0, ZoneOffset.UTC);
        return expiredDateTime.isBefore(time);
    }

    public boolean compareTokenKey(Long memberId) {
        if (this.memberId.equals(String.valueOf(memberId))) {
            return true;
        }

        return false;
    }

    public String makeActiveKey() {
        return this.memberId + ":" + this.expiredAt;
    }
}
