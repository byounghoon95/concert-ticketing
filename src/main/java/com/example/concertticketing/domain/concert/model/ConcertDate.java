package com.example.concertticketing.domain.concert.model;

import java.time.LocalDateTime;

public record ConcertDate(
        Long concertDetailId,
        LocalDateTime dates
) {
}
