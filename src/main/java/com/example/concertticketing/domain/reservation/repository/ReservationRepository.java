package com.example.concertticketing.domain.reservation.repository;

import com.example.concertticketing.domain.reservation.model.Reservation;

import java.util.List;

public interface ReservationRepository {

    Reservation reserveSeat(Reservation reservation);
    Reservation findById(Long reservationId);
    Reservation save(Reservation reservation);
    List<Reservation> findAll();
    void deleteAllInBatch();
    void saveAll(List<Reservation> reservationList);
}