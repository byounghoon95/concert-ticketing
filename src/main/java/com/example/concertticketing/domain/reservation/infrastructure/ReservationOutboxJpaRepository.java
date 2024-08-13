package com.example.concertticketing.domain.reservation.infrastructure;

import com.example.concertticketing.domain.message.model.OutboxStatus;
import com.example.concertticketing.domain.reservation.model.ReservationOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationOutboxJpaRepository extends JpaRepository<ReservationOutbox,Long> {
    Optional<ReservationOutbox> findByEventId(Long eventId);
    List<ReservationOutbox> findAllByStatus(OutboxStatus status);
}
