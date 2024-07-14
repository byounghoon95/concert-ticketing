package com.example.concertticketing.domain.concert.infrastructure;

import com.example.concertticketing.domain.concert.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SeatJpaRepository extends JpaRepository<Seat,Long> {
    List<Seat> findByConcertIdAndReservedAtIsNullOrReservedAtBefore(@Param("concertId") Long concertDetailId, @Param("time") LocalDateTime time);
}
