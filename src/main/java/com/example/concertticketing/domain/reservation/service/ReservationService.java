package com.example.concertticketing.domain.reservation.service;


import com.example.concertticketing.domain.concert.model.Seat;
import com.example.concertticketing.domain.reservation.model.Reservation;

public interface ReservationService {
    Reservation reserveSeat(Seat seat, Long memberId);

    Reservation findById(Long reservationId);
}
