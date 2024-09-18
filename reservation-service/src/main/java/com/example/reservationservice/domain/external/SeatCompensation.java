package com.example.reservationservice.domain.external;


import java.time.LocalDateTime;

public record SeatCompensation(Long seatId, Long memberId, LocalDateTime reservedAt) {
    public static SeatCompensation from(Seat seat) {
//        Long memberId = (seat.getMember() == null) ? null : seat.getMember().getId();
        return new SeatCompensation(seat.getId(), 1L, seat.getReservedAt());
    }
}
