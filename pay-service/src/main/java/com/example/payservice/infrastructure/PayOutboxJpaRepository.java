package com.example.payservice.infrastructure;

import com.example.payservice.domain.message.model.OutboxStatus;
import com.example.payservice.domain.model.PayOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayOutboxJpaRepository extends JpaRepository<PayOutbox,Long> {
    List<PayOutbox> findAllByStatus(OutboxStatus status);
    List<PayOutbox> findAllByEventIdAndStatus(Long eventId, OutboxStatus status);
}
