package com.example.concertticketing.api.reservation.dto;

import com.example.concertticketing.domain.reservation.model.Reservation;
import com.example.concertticketing.domain.reservation.model.ReservationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReserveSeatResponse {
    int seat;
    ReservationStatus status;

    @Builder
    public ReserveSeatResponse(int seat,ReservationStatus status) {
        this.seat = seat;
        this.status = status;
    }

    public static ReserveSeatResponse of(Reservation reservation) {
        return ReserveSeatResponse.builder()
                .seat(reservation.getSeatNo())
                .status(reservation.getStatus())
                .build();
    }
}
