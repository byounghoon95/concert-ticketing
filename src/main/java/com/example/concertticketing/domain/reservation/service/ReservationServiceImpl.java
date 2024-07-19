package com.example.concertticketing.domain.reservation.service;

import com.example.concertticketing.domain.concert.model.Seat;
import com.example.concertticketing.domain.concert.service.SeatService;
import com.example.concertticketing.domain.exception.CustomException;
import com.example.concertticketing.domain.exception.ErrorEnum;
import com.example.concertticketing.domain.member.model.Member;
import com.example.concertticketing.domain.member.service.MemberService;
import com.example.concertticketing.domain.reservation.model.Reservation;
import com.example.concertticketing.domain.reservation.model.ReservationStatus;
import com.example.concertticketing.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatService seatService;
    private final MemberService memberService;

    @Transactional
    @Override
    public Reservation reserveSeat(Long seatId, Long memberId) {
        Seat seat = seatService.selectSeat(seatId);

        // 5분동안 임시저장
        LocalDateTime reservedAt = seat.getReservedAt();
        Reservation.checkTempReserved(reservedAt);

        Member member = seat.getMember();
        if (seat.getMember() != null) {
            memberId = seat.getMember().getId();
        } else {
            member = memberService.findById(memberId);
        }

        seatService.reserveSeat(seatId, LocalDateTime.now(), member);

        Reservation reservation = Reservation.createReservation(seat, memberId);

        return reservationRepository.reserveSeat(reservation);
    }

    @Override
    public Reservation findById(Long reservationId) {
        return reservationRepository.findById(reservationId);
    }
}
