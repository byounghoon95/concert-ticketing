package com.example.concertticketing.api.pay.dto;

public record PayRequest(
        Long reservationId,
        Long seatId,
        Long memberId
) {

}
