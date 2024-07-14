package com.example.concertticketing.domain.concert.model;

import java.util.List;

public record ConcertDate(
        Long concertId,
        List<ConcertDateDetails> concertDates
) {
}
