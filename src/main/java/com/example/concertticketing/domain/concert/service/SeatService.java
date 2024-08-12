package com.example.concertticketing.domain.concert.service;


import com.example.concertticketing.domain.concert.model.Seat;
import com.example.concertticketing.domain.concert.model.SeatCompensation;

import java.time.LocalDateTime;

public interface SeatService {
    Seat selectSeat(Long seatId);

    void updateReservedAt(Long seatId, LocalDateTime now);

    void reserveSeat(Long seatId, LocalDateTime now, Long memberId);

    Seat selectSeatWithLock(Long seatId);

    void rollbackSeat(SeatCompensation seat);
}
