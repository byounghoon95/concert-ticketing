package com.example.reservationservice.domain.message.service;

import com.example.reservationservice.domain.message.model.OutboxDto;

public interface OutboxService {
    void save(OutboxDto dto);
    void published(Long eventId);
}
