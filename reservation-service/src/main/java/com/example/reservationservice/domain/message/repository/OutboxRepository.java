package com.example.reservationservice.domain.message.repository;

import com.example.reservationservice.domain.message.model.OutboxDto;
import com.example.reservationservice.domain.message.model.OutboxStatus;

import java.util.List;

public interface OutboxRepository<T> {
    void save(OutboxDto dto);
    List<T> findAllByStatus(OutboxStatus status);
    void deleteAllInBatch();
    List<T> findAll();
    List<T> findInitList(Long eventId);
}
