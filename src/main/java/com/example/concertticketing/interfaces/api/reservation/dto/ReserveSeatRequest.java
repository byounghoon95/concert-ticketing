package com.example.concertticketing.interfaces.api.reservation.dto;

public record ReserveSeatRequest(
        Long seatId,
        Long memberId
) {

}
