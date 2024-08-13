package com.example.concertticketing.domain.message.repository;

import com.example.concertticketing.domain.message.model.OutboxDto;

public interface OutboxRepository<T> {
    void save(OutboxDto dto);
    T findByEventId(Long eventId);
}
