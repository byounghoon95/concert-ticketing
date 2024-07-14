package com.example.concertticketing.domain.concert.infrastructure;

import com.example.concertticketing.domain.concert.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SeatJpaRepository extends JpaRepository<Seat,Long> {
    @Query("SELECT s FROM Seat s WHERE s.concert.id = :concertDetailId AND (s.reservedAt IS NULL OR s.reservedAt < :time)")
    List<Seat> findSeatsByConcertIdAndType(@Param("concertDetailId") Long concertDetailId, @Param("time") LocalDateTime time);
}
