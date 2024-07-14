package com.example.concertticketing.domain.concert.infrastructure;

import com.example.concertticketing.domain.concert.model.ConcertDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConcertDetailJpaRepository extends JpaRepository<ConcertDetail,Long> {
    List<ConcertDetail> findByConcertId(@Param("concertId") Long concertId);
}
