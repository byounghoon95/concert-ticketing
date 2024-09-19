package com.example.reservationservice.domain.repository;

import com.example.reservationservice.domain.model.OutboxDto;
import com.example.reservationservice.domain.model.OutboxStatus;
import com.example.reservationservice.domain.model.ReservationOutbox;

import java.util.List;

public interface ReservationOutboxRepository {
    void save(OutboxDto dto);
    List<ReservationOutbox> findAllByStatus(OutboxStatus status);
    void deleteAllInBatch();
    List<ReservationOutbox> findAll();
    List<ReservationOutbox> findInitList(Long eventId);
}
