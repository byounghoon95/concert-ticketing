package com.example.concertticketing.domain.pay.infrastructure;

import com.example.concertticketing.domain.pay.model.PayOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PayOutboxJpaRepository extends JpaRepository<PayOutbox,Long> {
    Optional<PayOutbox> findByEventId(Long eventId);
}
