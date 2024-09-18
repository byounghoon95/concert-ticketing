package com.example.reservationservice.domain.message.model;

import com.example.reservationservice.domain.model.ReservationOutbox;
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

    public ReservationOutbox toReservationOutbox() {
        return ReservationOutbox.builder()
                .eventId(eventId)
                .payload(payload)
                .status(OutboxStatus.INIT)
                .createdAt(LocalDateTime.now())
                .build();
    }

//    public PayOutbox toPayOutbox() {
//        return PayOutbox.builder()
//                .eventId(eventId)
//                .payload(payload)
//                .status(OutboxStatus.INIT)
//                .createdAt(LocalDateTime.now())
//                .build();
//    }
}
