package com.example.concertticketing.domain.message.model;

import com.example.concertticketing.domain.pay.model.PayOutbox;
import com.example.concertticketing.domain.reservation.model.ReservationOutbox;
import lombok.Builder;

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
                .build();
    }

    public PayOutbox toPayOutbox() {
        return PayOutbox.builder()
                .eventId(eventId)
                .payload(payload)
                .status(OutboxStatus.INIT)
                .build();
    }
}
