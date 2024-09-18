package com.example.concertservice.domain.service;

import com.example.concertservice.domain.model.ConcertDate;
import com.example.concertservice.domain.model.ConcertSeat;

public interface ConcertService {
    ConcertDate selectAvailableDates(Long concertId);

    ConcertSeat selectAvailableSeats(Long concertDetailId);
}
