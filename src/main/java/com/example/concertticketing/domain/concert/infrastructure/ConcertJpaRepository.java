package com.example.concertticketing.domain.concert.infrastructure;

import com.example.concertticketing.domain.concert.model.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertJpaRepository extends JpaRepository<Concert,Long> {
}
