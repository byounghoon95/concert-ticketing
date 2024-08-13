package com.example.concertticketing.domain.reservation.service;

import com.example.concertticketing.domain.concert.model.Seat;
import com.example.concertticketing.domain.concert.model.SeatCompensation;
import com.example.concertticketing.domain.concert.repository.SeatRepository;
import com.example.concertticketing.domain.reservation.event.ReservationEvent;
import com.example.concertticketing.domain.reservation.model.Reservation;
import com.example.concertticketing.domain.reservation.repository.ReservationRepository;
import com.example.concertticketing.exception.CustomException;
import com.example.concertticketing.exception.ErrorEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public Reservation reserveSeat(Long seatId, Long memberId, SeatCompensation seatComp) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new CustomException(ErrorEnum.NO_SEAT));

        Reservation reservation = Reservation.createReservation(seat, memberId);

        Reservation savedReservation = reservationRepository.reserveSeat(reservation);

        eventPublisher.publishEvent(ReservationEvent.from(savedReservation,seatComp));

        return savedReservation;
    }

    @Override
    public Reservation findById(Long reservationId) {
        return reservationRepository.findById(reservationId);
    }
}
