package com.example.concertticketing.interfaces.api.reservation.dto;

import com.example.concertticketing.domain.reservation.model.Reservation;
import com.example.concertticketing.domain.reservation.model.ReservationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReserveSeatResponse {
    int seatNo;
    ReservationStatus status;

    @Builder
    public ReserveSeatResponse(int seatNo,ReservationStatus status) {
        this.seatNo = seatNo;
        this.status = status;
    }

    public static ReserveSeatResponse of(Reservation reservation) {
        return ReserveSeatResponse.builder()
                .seatNo(reservation.getSeatNo())
                .status(reservation.getStatus())
                .build();
    }
}
