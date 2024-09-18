package com.example.payservice.interfaces.api.dto;

import com.example.payservice.domain.model.Pay;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PayResponse {
    Long amount;
    int seatNo;

    @Builder
    public PayResponse(Long amount, int seatNo) {
        this.amount = amount;
        this.seatNo = seatNo;
    }

    public static PayResponse of(Pay pay) {
        return PayResponse.builder()
//                .seatNo(pay.getReservation().getSeatNo())
                .amount(pay.getAmount())
                .build();
    }
}
