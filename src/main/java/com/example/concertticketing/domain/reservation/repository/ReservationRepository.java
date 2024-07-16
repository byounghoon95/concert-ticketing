package com.example.concertticketing.domain.reservation.repository;

import com.example.concertticketing.domain.reservation.model.Reservation;

public interface ReservationRepository {

    Reservation reserveSeat(Reservation reservation);
    Reservation findById(Long reservationId);
    Reservation save(Reservation reservation);

    void deleteAllInBatch();
}
