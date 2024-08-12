package com.example.concertticketing.domain.concert.model;

import java.time.LocalDateTime;

public record SeatCompensation(Long seatId, Long memberId, LocalDateTime reservedAt) {
    public static SeatCompensation from(Seat seat) {
        Long memberId = (seat.getMember() == null) ? null : seat.getMember().getId();
        return new SeatCompensation(seat.getId(), memberId, seat.getReservedAt());
    }
}
