package com.example.concertticketing.domain.concert.repository;


import com.example.concertticketing.domain.concert.model.Seat;

import java.util.List;
import java.util.Optional;

public interface SeatRepository {
    Optional<Seat> findById(Long seatId);
    Optional<Seat> selectSeatWithLock(Long seatId);
    Seat save(Seat seat);
    void saveAll(List<Seat> seats);
    void deleteAllInBatch();
}
