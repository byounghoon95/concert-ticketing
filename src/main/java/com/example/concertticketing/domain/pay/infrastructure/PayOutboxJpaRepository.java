package com.example.concertticketing.domain.pay.infrastructure;

import com.example.concertticketing.domain.message.model.OutboxStatus;
import com.example.concertticketing.domain.pay.model.PayOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PayOutboxJpaRepository extends JpaRepository<PayOutbox,Long> {
    Optional<PayOutbox> findByEventId(Long eventId);
    List<PayOutbox> findAllByStatus(OutboxStatus status);
}
