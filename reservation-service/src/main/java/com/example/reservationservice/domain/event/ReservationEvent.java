package com.example.reservationservice.domain.event;


import com.example.reservationservice.domain.external.SeatCompensation;
import com.example.reservationservice.domain.model.Reservation;

public record ReservationEvent(Long reservationId, int seatNo, Long memberId, Long price, SeatCompensation seat) {
    public String payload() {
        return String.format("유저 아이디 %d가 좌석번호 %d를 %d 예약 완료 하였습니다.", memberId, seatNo, price);
    }

    public static ReservationEvent from(Reservation reservation, SeatCompensation seat) {
        return new ReservationEvent(reservation.getId(), reservation.getSeatNo(), reservation.getMemberId(), reservation.getPrice(), seat);
    }
}