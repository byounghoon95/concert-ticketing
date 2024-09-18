package com.example.reservationservice.infrastructure;

import com.example.reservationservice.domain.message.model.OutboxStatus;
import com.example.reservationservice.domain.model.ReservationOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationOutboxJpaRepository extends JpaRepository<ReservationOutbox,Long> {
    List<ReservationOutbox> findAllByStatus(OutboxStatus status);
    List<ReservationOutbox> findAllByEventIdAndStatus(Long eventId, OutboxStatus status);
}
