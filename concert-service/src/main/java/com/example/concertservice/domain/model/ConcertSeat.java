package com.example.concertservice.domain.model;

import java.util.List;

public record ConcertSeat(
        Long concertDetailId,
        List<ConcertSeatDetail> concertSeats
) {
}
