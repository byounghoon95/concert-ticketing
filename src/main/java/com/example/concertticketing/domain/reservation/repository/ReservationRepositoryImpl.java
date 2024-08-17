package com.example.concertticketing.domain.reservation.repository;

import com.example.concertticketing.domain.reservation.infrastructure.ReservationJpaRepository;
import com.example.concertticketing.domain.reservation.model.Reservation;
import com.example.concertticketing.exception.CustomException;
import com.example.concertticketing.exception.ErrorEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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
                .orElseThrow(() -> new CustomException(ErrorEnum.NO_RESERVATION));
    }

    @Override
    public Reservation save(Reservation reservation) {
        return reservationJpaRepository.save(reservation);
    }

    @Override
    public List<Reservation> findAll() {
        return reservationJpaRepository.findAll();
    }

    @Override
    public void deleteAllInBatch() {
        reservationJpaRepository.deleteAllInBatch();
    }

    @Override
    public void saveAll(List<Reservation> reservationList) {
        reservationJpaRepository.saveAll(reservationList);
    }
}
