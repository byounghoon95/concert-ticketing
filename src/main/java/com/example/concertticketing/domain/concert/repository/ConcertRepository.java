package com.example.concertticketing.domain.concert.repository;


import com.example.concertticketing.domain.concert.model.ConcertDetail;
import com.example.concertticketing.domain.concert.model.Seat;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertRepository {

    List<ConcertDetail> findDatesByConcertId(Long concertId);

    List<Seat> findAvailableSeatsByConcertId(Long concertDetailId, LocalDateTime time);
}
