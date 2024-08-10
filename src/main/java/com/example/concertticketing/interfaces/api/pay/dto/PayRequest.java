package com.example.concertticketing.interfaces.api.pay.dto;

public record PayRequest(
        Long reservationId,
        Long seatId,
        Long memberId
) {

}
