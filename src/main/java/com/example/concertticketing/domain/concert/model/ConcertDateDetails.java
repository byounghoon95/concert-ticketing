package com.example.concertticketing.domain.concert.model;

import java.time.LocalDateTime;

public record ConcertDateDetails(
        Long concertDetailId,
        LocalDateTime dates
) {
}
