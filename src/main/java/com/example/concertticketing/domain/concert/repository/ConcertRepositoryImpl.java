package com.example.concertticketing.domain.concert.repository;

import com.example.concertticketing.domain.concert.infrastructure.ConcertDetailJpaRepository;
import com.example.concertticketing.domain.concert.infrastructure.SeatJpaRepository;
import com.example.concertticketing.domain.concert.model.ConcertDetail;
import com.example.concertticketing.domain.concert.model.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class ConcertRepositoryImpl implements ConcertRepository {

    private final ConcertDetailJpaRepository concertDetailJpaRepository;
    private final SeatJpaRepository seatJpaRepository;

    @Override
    public List<ConcertDetail> findDatesByConcertId(Long concertId) {
        return concertDetailJpaRepository.findByConcertId(concertId);
    }

    @Override
    public List<Seat> findAvailableSeatsByConcertId(Long concertDetailId, LocalDateTime time) {
        return seatJpaRepository.findByConcertIdAndReservedAtIsNullOrReservedAtBefore(concertDetailId, time);
    }
}
