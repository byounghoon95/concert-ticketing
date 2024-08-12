package com.example.concertticketing.domain.reservation.service;


import com.example.concertticketing.domain.concert.model.SeatCompensation;
import com.example.concertticketing.domain.reservation.model.Reservation;

public interface ReservationService {
    Reservation reserveSeat(Long seatId, Long memberId, SeatCompensation seat);

    Reservation findById(Long reservationId);
}
