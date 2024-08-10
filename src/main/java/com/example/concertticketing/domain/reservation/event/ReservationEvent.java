package com.example.concertticketing.domain.reservation.event;

import com.example.concertticketing.domain.reservation.model.Reservation;

public record ReservationEvent(int seatNo, Long memberId, Long price) {
    public String payload() {
        return String.format("유저 아이디 %d가 좌석번호 %d를 %d 예약 완료 하였습니다.", memberId, seatNo, price);
    }

    public static ReservationEvent from(Reservation reservation) {
        return new ReservationEvent(reservation.getSeatNo(), reservation.getMemberId(), reservation.getPrice());
    }
}