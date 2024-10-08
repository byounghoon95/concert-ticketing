package com.example.concertservice.domain.repository;


import com.example.concertservice.domain.model.Concert;
import com.example.concertservice.domain.model.ConcertDate;
import com.example.concertservice.domain.model.ConcertDetail;
import com.example.concertservice.domain.model.Seat;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertRepository {
    List<ConcertDetail> findConcertDates(Long concertId);
    List<Seat> findAvailableSeats(Long concertDetailId, LocalDateTime time);
    Concert saveConcert(Concert concert);
    void deleteAllInBatch();
    void saveConcertDetailAll(List<ConcertDetail> detailList);
    ConcertDate findConcertDatesFromCache(Long concertId);
    void addCache(Long concertId,ConcertDate concertDate);
    ConcertDetail findConcertDetail(Long concertDetailId);
}
