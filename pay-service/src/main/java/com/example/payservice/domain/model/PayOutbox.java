package com.example.payservice.domain.model;

import com.example.payservice.domain.message.model.OutboxStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PAY_OUTBOX", indexes = {
        @Index(name = "idx_eventId", columnList = "eventId")
})
@Entity
public class PayOutbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "EVENT_ID")
    private Long eventId;

    @Column(name = "PAYLOAD")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private OutboxStatus status;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Builder
    public PayOutbox(Long eventId, String payload, OutboxStatus status, LocalDateTime createdAt) {
        this.eventId = eventId;
        this.payload = payload;
        this.status = status;
        this.createdAt = createdAt;
    }

    public void published() {
        this.status = OutboxStatus.PUBLISHED;
    }

    public boolean isPublished() {
        if (status == OutboxStatus.INIT && createdAt.isBefore(LocalDateTime.now().minusMinutes(5))) {
            return false;
        }

        return true;
    }
}
