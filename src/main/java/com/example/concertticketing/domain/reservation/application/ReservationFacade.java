package com.example.concertticketing.domain.reservation.application;

import com.example.concertticketing.domain.concert.model.Seat;
import com.example.concertticketing.domain.concert.service.SeatService;
import com.example.concertticketing.domain.reservation.model.Reservation;
import com.example.concertticketing.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class ReservationFacade {
    private final SeatService seatService;
    private final ReservationService reservationService;

    public Reservation reserveSeat(Long seatId, Long memberId) {
        Seat seat = seatService.selectSeatWithLock(seatId);

        // 5분동안 임시저장
        LocalDateTime reservedAt = seat.getReservedAt();
        Reservation.checkTempReserved(reservedAt);

        seatService.reserveSeat(seatId, LocalDateTime.now(), memberId);

        return reservationService.reserveSeat(seatId, memberId);
    }

    public Reservation findById(Long reservationId) {
        return reservationService.findById(reservationId);
    }
}
