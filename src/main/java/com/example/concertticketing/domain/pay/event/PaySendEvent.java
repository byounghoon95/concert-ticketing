package com.example.concertticketing.domain.pay.event;

import com.example.concertticketing.domain.pay.model.Pay;
import com.example.concertticketing.domain.pay.model.PayStatus;

public record PaySendEvent(Long payId, Long reservationId, Long amount, PayStatus status) {
    public String payload() {
        return String.format("예약 아이디 %d, %d 금액 결제가 완료 되었습니다.", reservationId, amount);
    }

    public static PaySendEvent from(Pay pay) {
        return new PaySendEvent(pay.getId(), pay.getReservation().getId(), pay.getAmount(), pay.getStatus());
    }
}

