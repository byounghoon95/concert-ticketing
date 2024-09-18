package com.example.payservice.interfaces.api.dto;

public record PayRequest(
        Long reservationId,
        Long seatId,
        Long memberId
) {

}
