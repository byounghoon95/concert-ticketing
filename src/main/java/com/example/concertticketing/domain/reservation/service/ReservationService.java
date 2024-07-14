package com.example.concertticketing.domain.reservation.service;


import com.example.concertticketing.domain.reservation.model.Reservation;

public interface ReservationService {
    Reservation reserveSeat(Long seatId);

    Reservation findById(Long reservationId);
}
