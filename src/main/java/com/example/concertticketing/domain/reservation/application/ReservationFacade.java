package com.example.concertticketing.domain.reservation.application;

import com.example.concertticketing.domain.concert.model.Seat;
import com.example.concertticketing.domain.concert.service.SeatService;
import com.example.concertticketing.domain.exception.CustomException;
import com.example.concertticketing.domain.exception.ErrorEnum;
import com.example.concertticketing.domain.reservation.model.Reservation;
import com.example.concertticketing.domain.reservation.service.ReservationService;
import com.example.concertticketing.domain.util.LettuceLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class ReservationFacade {
    private final SeatService seatService;
    private final ReservationService reservationService;
    private final LettuceLock lettuceLock;

    public Reservation reserveSeat(Long seatId, Long memberId) {
        try {
            Boolean lock = lettuceLock.lock(seatId);
            if (!lock) {
                throw new CustomException(ErrorEnum.RESERVED_SEAT);
            }

            Seat seat = seatService.selectSeat(seatId);

            // 5분동안 임시저장
            LocalDateTime reservedAt = seat.getReservedAt();
            Reservation.checkTempReserved(reservedAt);

            if (seat.getMember() != null) {
                Reservation.checkMember(memberId, seat.getMember().getId());
            }

            seatService.reserveSeat(seatId, LocalDateTime.now(), memberId);
        } finally {
            lettuceLock.unlock(seatId);
        }
        return reservationService.reserveSeat(seatId, memberId);
    }

    public Reservation findById(Long reservationId) {
        return reservationService.findById(reservationId);
    }
}
