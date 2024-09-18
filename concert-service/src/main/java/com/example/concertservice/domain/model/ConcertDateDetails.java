package com.example.concertservice.domain.model;

import java.time.LocalDateTime;

public record ConcertDateDetails(
        Long concertDetailId,
        LocalDateTime dates
) {
}
