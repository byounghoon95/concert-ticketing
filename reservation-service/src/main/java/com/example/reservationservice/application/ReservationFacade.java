package com.example.reservationservice.application;

import com.example.reservationservice.domain.model.Reservation;
import com.example.reservationservice.domain.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ReservationFacade {
//    private final SeatService seatService;
    private final ReservationService reservationService;

    public Reservation reserveSeat(Long seatId, Long memberId) {
//        Seat seat = seatService.selectSeatWithLock(seatId);
//        SeatCompensation seatComp = SeatCompensation.from(seat);

        // 5분동안 임시저장
//        LocalDateTime reservedAt = seat.getReservedAt();
//        Reservation.checkTempReserved(reservedAt);

//        seatService.reserveSeat(seatId, LocalDateTime.now(), memberId);

        return Reservation.createReservation(memberId);
//        return reservationService.reserveSeat(seatId, memberId, seatComp);
    }

    public Reservation findById(Long reservationId) {
        return reservationService.findById(reservationId);
    }
}
