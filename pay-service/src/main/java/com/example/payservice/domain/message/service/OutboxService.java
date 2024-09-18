package com.example.payservice.domain.message.service;

import com.example.payservice.domain.message.model.OutboxDto;

public interface OutboxService {
    void save(OutboxDto dto);
    void published(Long eventId);
}
