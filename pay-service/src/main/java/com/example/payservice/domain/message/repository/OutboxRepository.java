package com.example.payservice.domain.message.repository;

import com.example.payservice.domain.message.model.OutboxDto;
import com.example.payservice.domain.message.model.OutboxStatus;

import java.util.List;

public interface OutboxRepository<T> {
    void save(OutboxDto dto);
    List<T> findAllByStatus(OutboxStatus status);
    void deleteAllInBatch();
    List<T> findAll();
    List<T> findInitList(Long eventId);
}
