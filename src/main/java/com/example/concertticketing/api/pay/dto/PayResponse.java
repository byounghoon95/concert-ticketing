package com.example.concertticketing.api.pay.dto;

import com.example.concertticketing.domain.pay.model.Pay;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PayResponse {
    Long amount;
    int seat;

    @Builder
    public PayResponse(Long amount, int seat) {
        this.amount = amount;
        this.seat = seat;
    }

    public static PayResponse of(Pay pay) {
        return PayResponse.builder()
                .seat(pay.getReservation().getSeatNo())
                .amount(pay.getAmount())
                .build();
    }
}
