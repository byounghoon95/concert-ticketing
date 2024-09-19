package com.example.reservationservice.domain.external;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class SeatResponse {
    private Long id;
    private Long memberId;
    private Long concertDetailId;
    private int seatNo;
    private Long price;
    private LocalDateTime reservedAt;

    public SeatResponse(Long id, Long memberId, Long concertDetailId, int seatNo, Long price, LocalDateTime reservedAt) {
        this.id = id;
        this.memberId = memberId;
        this.concertDetailId = concertDetailId;
        this.seatNo = seatNo;
        this.price = price;
        this.reservedAt = reservedAt;
    }
}