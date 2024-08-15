package com.example.concertticketing.domain.reservation.infrastructure;

import com.example.concertticketing.domain.message.model.OutboxStatus;
import com.example.concertticketing.domain.reservation.model.ReservationOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationOutboxJpaRepository extends JpaRepository<ReservationOutbox,Long> {
    List<ReservationOutbox> findAllByStatus(OutboxStatus status);
    List<ReservationOutbox> findAllByEventIdAndStatus(Long eventId, OutboxStatus status);
}
