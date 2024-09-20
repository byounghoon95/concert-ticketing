package com.example.concertservice.domain.service;


import com.example.concertservice.domain.model.Seat;
import com.example.concertservice.domain.model.SeatCompensation;

import java.time.LocalDateTime;

public interface SeatService {
    Seat selectSeat(Long seatId);

    void updateReservedAt(Long seatId, LocalDateTime now);

    void reserveSeat(Long seatId, LocalDateTime now, Long memberId);

    Seat selectSeatWithLock(Long seatId);

    void rollbackSeat(SeatCompensation seat);

    void confirmSeat(Long seatId);
}
