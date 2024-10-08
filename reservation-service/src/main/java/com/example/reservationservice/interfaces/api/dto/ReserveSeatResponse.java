package com.example.reservationservice.interfaces.api.dto;

import com.example.reservationservice.domain.model.Reservation;
import com.example.reservationservice.domain.model.ReservationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReserveSeatResponse {
    Long id;
    int seatNo;
    ReservationStatus status;

    @Builder
    public ReserveSeatResponse(Long id, int seatNo,ReservationStatus status) {
        this.id = id;
        this.seatNo = seatNo;
        this.status = status;
    }

    public static ReserveSeatResponse of(Reservation reservation) {
        return ReserveSeatResponse.builder()
                .id(reservation.getId())
                .seatNo(reservation.getSeatNo())
                .status(reservation.getStatus())
                .build();
    }
}
