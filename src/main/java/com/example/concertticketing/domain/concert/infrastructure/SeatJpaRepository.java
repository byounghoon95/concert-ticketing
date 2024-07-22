package com.example.concertticketing.domain.concert.infrastructure;

import com.example.concertticketing.domain.concert.model.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SeatJpaRepository extends JpaRepository<Seat,Long> {
    List<Seat> findByConcertIdAndReservedAtIsNullOrReservedAtBefore(@Param("concertId") Long concertDetailId, @Param("time") LocalDateTime time);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT s FROM Seat s WHERE s.id = :seatId")
    Optional<Seat> selectSeatWithLock(@Param("seatId") Long seatId);
}
