package com.example.concertticketing.domain.reservation.model;

import com.example.concertticketing.domain.message.model.OutboxStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "RESERVATION_OUTBOX")
@Entity
public class ReservationOutbox {
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
    public ReservationOutbox(Long eventId, String payload, OutboxStatus status) {
        this.eventId = eventId;
        this.payload = payload;
        this.status = status;
    }

    public void published() {
        this.status = OutboxStatus.PUBLISHED;
    }
}
