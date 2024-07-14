package com.example.concertticketing.domain.reservation.repository;

import com.example.concertticketing.domain.reservation.infrastructure.ReservationJpaRepository;
import com.example.concertticketing.domain.reservation.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ReservationRepositoryImpl implements ReservationRepository {

    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public Reservation reserveSeat(Reservation reservation) {
        return reservationJpaRepository.save(reservation);
    }

    @Override
    public Reservation findById(Long reservationId) {
        return reservationJpaRepository.findById(reservationId)
                .orElseThrow(() -> new NullPointerException("일치하는 예약번호가 없습니다"));
    }
}
