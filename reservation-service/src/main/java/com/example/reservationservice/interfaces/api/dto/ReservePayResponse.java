package com.example.reservationservice.interfaces.api.dto;

import com.example.reservationservice.domain.model.Reservation;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservePayResponse {
    private Long reservationId;
    private Long price;

    @Builder
    public ReservePayResponse(Long reservationId, Long price) {
        this.reservationId = reservationId;
        this.price = price;
    }

    public static ReservePayResponse of(Reservation reservation) {
        return ReservePayResponse.builder()
                .reservationId(reservation.getId())
                .price(reservation.getPrice())
                .build();
    }
}
