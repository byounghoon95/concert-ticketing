package com.example.reservationservice.domain.service;

import com.example.reservationservice.domain.model.OutboxDto;

public interface ReservationOutboxService {
    void save(OutboxDto dto);
    void published(Long eventId);
}
