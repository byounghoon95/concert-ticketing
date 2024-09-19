package com.example.reservationservice.domain.external;


import java.time.LocalDateTime;

public record SeatCompensation(Long seatId, Long memberId, LocalDateTime reservedAt) {
    public static SeatCompensation from(SeatResponse seat) {
        Long memberId = (seat.getMemberId() == null) ? null : seat.getMemberId();
        return new SeatCompensation(seat.getId(), memberId, seat.getReservedAt());
    }
}
