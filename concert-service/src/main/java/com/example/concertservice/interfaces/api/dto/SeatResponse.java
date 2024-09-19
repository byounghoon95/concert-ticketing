package com.example.concertservice.interfaces.api.dto;

import com.example.concertservice.domain.model.Seat;
import lombok.Builder;
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

    @Builder
    public SeatResponse(Long id, Long memberId, Long concertDetailId, int seatNo, Long price, LocalDateTime reservedAt) {
        this.id = id;
        this.memberId = memberId;
        this.concertDetailId = concertDetailId;
        this.seatNo = seatNo;
        this.price = price;
        this.reservedAt = reservedAt;
    }

    public static SeatResponse of(Seat seat, Long concertDetailId) {
        return SeatResponse.builder()
                .id(seat.getId())
                .memberId(seat.getMemberId())
                .concertDetailId(concertDetailId)
                .seatNo(seat.getSeatNo())
                .price(seat.getPrice())
                .reservedAt(seat.getReservedAt())
                .build();
    }
}
