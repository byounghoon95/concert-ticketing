package com.example.concertticketing.domain.message.repository;

import com.example.concertticketing.domain.message.model.OutboxDto;
import com.example.concertticketing.domain.message.model.OutboxStatus;

import java.util.List;

public interface OutboxRepository<T> {
    void save(OutboxDto dto);
    T findByEventId(Long eventId);
    List<T> findAllByStatus(OutboxStatus status);
}
