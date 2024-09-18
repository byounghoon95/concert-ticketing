package com.example.reservationservice.interfaces.api.dto;

public record ReserveSeatRequest(
        Long seatId,
        Long memberId
) {

}
