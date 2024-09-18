package com.example.payservice.domain.event;

import com.example.payservice.domain.model.Pay;
import com.example.payservice.domain.model.PayStatus;

public record PayMessageEvent(Long payId, Long reservationId, Long amount, PayStatus status) {
    public String payload() {
        return String.format("예약 아이디 %d, %d 금액 결제가 완료 되었습니다.", reservationId, amount);
    }

    public static PayMessageEvent from(Pay pay) {
        return new PayMessageEvent(pay.getId(), 1L, pay.getAmount(), pay.getStatus());
//        return new PayMessageEvent(pay.getId(), pay.getReservation().getId(), pay.getAmount(), pay.getStatus());
    }
}

