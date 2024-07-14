package com.example.concertticketing.domain.concert.model;

import java.util.List;

public record ConcertSeat(
        Long concertDetailId,
        List<ConcertSeatDetail> concertSeats
) {
}
