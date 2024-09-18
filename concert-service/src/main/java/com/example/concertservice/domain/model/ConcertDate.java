package com.example.concertservice.domain.model;

import java.util.List;

public record ConcertDate(
        Long concertId,
        List<ConcertDateDetails> concertDates
) {
}
