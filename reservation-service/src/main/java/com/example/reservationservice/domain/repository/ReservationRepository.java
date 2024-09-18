package com.example.reservationservice.domain.repository;

import com.example.reservationservice.domain.model.Reservation;

import java.util.List;

public interface ReservationRepository {

    Reservation reserveSeat(Reservation reservation);
    Reservation findById(Long reservationId);
    Reservation save(Reservation reservation);
    List<Reservation> findAll();
    void deleteAllInBatch();
    void saveAll(List<Reservation> reservationList);
}