package com.example.concertservice.infrastructure;

import com.example.concertservice.domain.model.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SeatJpaRepository extends JpaRepository<Seat,Long> {
    @Query("SELECT s FROM Seat s WHERE s.concert.id = :concertDetailId AND (s.reservedAt IS NULL OR s.reservedAt < :time)")
    List<Seat> findAvailableSeats(@Param("concertDetailId") Long concertDetailId, @Param("time") LocalDateTime time);

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("SELECT s FROM Seat s WHERE s.id = :seatId")
    Optional<Seat> selectSeatWithLock(@Param("seatId") Long seatId);
}
