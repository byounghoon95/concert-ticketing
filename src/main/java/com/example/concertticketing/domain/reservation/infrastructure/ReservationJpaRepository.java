package com.example.concertticketing.domain.reservation.infrastructure;

import com.example.concertticketing.domain.reservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationJpaRepository extends JpaRepository<Reservation,Long> {
}
