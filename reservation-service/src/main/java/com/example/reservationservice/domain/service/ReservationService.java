package com.example.reservationservice.domain.service;


import com.example.reservationservice.domain.external.SeatCompensation;
import com.example.reservationservice.domain.model.Reservation;

public interface ReservationService {
    Reservation reserveSeat(Long seatId, Long memberId, SeatCompensation seat);

    Reservation findById(Long reservationId);

    void republish();

    Reservation verifyReservation(Long reservationId, Long memberId);
}
