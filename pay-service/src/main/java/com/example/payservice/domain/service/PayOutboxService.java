package com.example.payservice.domain.service;

import com.example.payservice.domain.message.model.OutboxDto;

public interface PayOutboxService {
    void save(OutboxDto dto);
    void published(Long eventId);
}
