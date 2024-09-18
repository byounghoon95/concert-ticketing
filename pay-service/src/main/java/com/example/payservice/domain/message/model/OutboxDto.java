package com.example.payservice.domain.message.model;

import com.example.payservice.domain.model.PayOutbox;
import lombok.Builder;

import java.time.LocalDateTime;

public class OutboxDto {

    private Long eventId;
    private String payload;

    @Builder
    public OutboxDto(Long eventId, String payload) {
        this.eventId = eventId;
        this.payload = payload;
    }

//    public ReservationOutbox toReservationOutbox() {
//        return ReservationOutbox.builder()
//                .eventId(eventId)
//                .payload(payload)
//                .status(OutboxStatus.INIT)
//                .createdAt(LocalDateTime.now())
//                .build();
//    }

    public PayOutbox toPayOutbox() {
        return PayOutbox.builder()
                .eventId(eventId)
                .payload(payload)
                .status(OutboxStatus.INIT)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
