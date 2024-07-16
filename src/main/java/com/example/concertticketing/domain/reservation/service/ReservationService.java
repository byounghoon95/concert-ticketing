package com.example.concertticketing.domain.reservation.service;


import com.example.concertticketing.domain.reservation.model.Reservation;

public interface ReservationService {
    Reservation reserveSeat(Long seatId, Long memberId);

    Reservation findById(Long reservationId);
}
