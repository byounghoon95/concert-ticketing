package com.example.reservationservice.application;

import com.example.reservationservice.domain.clients.SeatClient;
import com.example.reservationservice.domain.external.SeatCompensation;
import com.example.reservationservice.domain.external.SeatResponse;
import com.example.reservationservice.domain.model.Reservation;
import com.example.reservationservice.domain.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class ReservationFacade {
    private final SeatClient seatClient;
    private final ReservationService reservationService;

    public Reservation reserveSeat(Long seatId, Long memberId) {
        SeatResponse seat = seatClient.findById(seatId).getData();
        SeatCompensation seatComp = SeatCompensation.from(seat);

        // 5분동안 임시저장
        LocalDateTime reservedAt = seat.getReservedAt();
        Reservation.checkTempReserved(reservedAt);

        seatClient.reserveSeat(seatId, memberId);

        return reservationService.reserveSeat(seatId, memberId, seatComp);
    }

    public Reservation findById(Long reservationId) {
        return reservationService.findById(reservationId);
    }
}
