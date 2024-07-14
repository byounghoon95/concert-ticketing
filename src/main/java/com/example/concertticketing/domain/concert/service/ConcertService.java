package com.example.concertticketing.domain.concert.service;

import com.example.concertticketing.domain.concert.model.ConcertDate;
import com.example.concertticketing.domain.concert.model.ConcertSeat;

public interface ConcertService {
    ConcertDate selectAvailableDates(Long concertId);

    ConcertSeat selectAvailableSeats(Long concertDetailId);
}
