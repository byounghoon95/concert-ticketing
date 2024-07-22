package com.example.concertticketing.domain.reservation.application;

import com.example.concertticketing.domain.concert.model.Seat;
import com.example.concertticketing.domain.concert.service.SeatService;
import com.example.concertticketing.domain.exception.CustomException;
import com.example.concertticketing.domain.exception.ErrorEnum;
import com.example.concertticketing.domain.member.model.Member;
import com.example.concertticketing.domain.member.service.MemberService;
import com.example.concertticketing.domain.reservation.model.Reservation;
import com.example.concertticketing.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class ReservationFacade {
    private final SeatService seatService;
    private final MemberService memberService;
    private final ReservationService reservationService;

    @Transactional
    public Reservation reserveSeat(Long seatId, Long memberId) {
        Seat seat = seatService.selectSeatWithLock(seatId);

        // 5분동안 임시저장
        LocalDateTime reservedAt = seat.getReservedAt();
        Reservation.checkTempReserved(reservedAt);

        Member member = seat.getMember();
        if (seat.getMember() != null) {
            memberId = seat.getMember().getId();
        } else {
            member = memberService.findById(memberId);
        }

        seatService.reserveSeat(seat, LocalDateTime.now(), member);
        return reservationService.reserveSeat(seat, memberId);
    }

    public Reservation findById(Long reservationId) {
        return reservationService.findById(reservationId);
    }
}
