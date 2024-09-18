package com.example.concertservice.infrastructure;

import com.example.concertservice.domain.model.ConcertDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConcertDetailJpaRepository extends JpaRepository<ConcertDetail,Long> {
    List<ConcertDetail> findByConcertId(@Param("concertId") Long concertId);
}
