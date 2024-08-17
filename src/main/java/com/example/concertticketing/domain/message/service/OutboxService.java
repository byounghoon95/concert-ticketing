package com.example.concertticketing.domain.message.service;

import com.example.concertticketing.domain.message.model.OutboxDto;

public interface OutboxService {
    void save(OutboxDto dto);
    void published(Long eventId);
}
