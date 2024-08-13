package com.example.concertticketing.domain.reservation.event;

import com.example.concertticketing.domain.concert.model.SeatCompensation;
import com.example.concertticketing.domain.reservation.model.Reservation;

public record ReservationEvent(Long reservationId, int seatNo, Long memberId, Long price, SeatCompensation seat) {
    public String payload() {
        return String.format("유저 아이디 %d가 좌석번호 %d를 %d 예약 완료 하였습니다.", memberId, seatNo, price);
    }

    public static ReservationEvent from(Reservation reservation, SeatCompensation seat) {
        return new ReservationEvent(reservation.getId(), reservation.getSeatNo(), reservation.getMemberId(), reservation.getPrice(), seat);
    }
}