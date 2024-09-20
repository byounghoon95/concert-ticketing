package com.example.concertservice.domain.service;

import com.example.concertservice.domain.model.ConcertDate;
import com.example.concertservice.domain.model.ConcertSeat;
import com.example.concertservice.interfaces.api.dto.SeatResponse;

public interface ConcertService {
    ConcertDate selectAvailableDates(Long concertId);

    ConcertSeat selectAvailableSeats(Long concertDetailId);

    SeatResponse getSeatById(Long seatId);
}
