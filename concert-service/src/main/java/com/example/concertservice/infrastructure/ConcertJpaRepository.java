package com.example.concertservice.infrastructure;

import com.example.concertservice.domain.model.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertJpaRepository extends JpaRepository<Concert,Long> {
}
