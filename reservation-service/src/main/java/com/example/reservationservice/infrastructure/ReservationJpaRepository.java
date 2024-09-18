package com.example.reservationservice.infrastructure;


import com.example.reservationservice.domain.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationJpaRepository extends JpaRepository<Reservation,Long> {
}
