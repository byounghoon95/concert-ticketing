package com.example.concertticketing.domain.concert.repository;


import com.example.concertticketing.domain.concert.model.Concert;
import com.example.concertticketing.domain.concert.model.ConcertDetail;
import com.example.concertticketing.domain.concert.model.Seat;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertRepository {
    List<ConcertDetail> findConcertDates(Long concertId);
    List<Seat> findAvailableSeats(Long concertDetailId, LocalDateTime time);
    void saveConcert(Concert concert);
    void deleteAllInBatch();

    void saveConcertDetailAll(List<ConcertDetail> detailList);

}
