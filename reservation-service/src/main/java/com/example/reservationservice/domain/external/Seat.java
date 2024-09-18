package com.example.reservationservice.domain.external;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Seat {
    private Long id;
//    private Member member;
//    private ConcertDetail concert;
    private int seatNo;
    private Long price;
    private LocalDateTime reservedAt;
}
