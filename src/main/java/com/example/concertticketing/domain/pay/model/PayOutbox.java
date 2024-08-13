package com.example.concertticketing.domain.pay.model;

import com.example.concertticketing.domain.common.entity.BaseEntity;
import com.example.concertticketing.domain.message.model.OutboxStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Getter
@Where(clause = "DELETED_AT IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PAY_OUTBOX")
@Entity
public class PayOutbox extends BaseEntity {
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

    @Builder
    public PayOutbox(Long eventId, String payload, OutboxStatus status) {
        this.eventId = eventId;
        this.payload = payload;
        this.status = status;
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

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
