package com.example.concertservice.domain.repository;


import com.example.concertservice.domain.model.Seat;

import java.util.List;
import java.util.Optional;

public interface SeatRepository {
    Optional<Seat> findById(Long seatId);
    Optional<Seat> selectSeatWithLock(Long seatId);
    Seat save(Seat seat);
    void saveAll(List<Seat> seats);
    void deleteAllInBatch();
}
