package com.example.concertticketing.domain.concert.repository;


import com.example.concertticketing.domain.concert.model.Seat;

import java.util.Optional;

public interface SeatRepository {
    Optional<Seat> findById(Long seatId);
    Seat save(Seat seat);
}
