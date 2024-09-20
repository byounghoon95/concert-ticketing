package com.example.payservice.domain.repository;

import com.example.payservice.domain.message.model.OutboxDto;
import com.example.payservice.domain.message.model.OutboxStatus;
import com.example.payservice.domain.model.PayOutbox;

import java.util.List;

public interface PayOutboxRepository {
    void save(OutboxDto dto);
    List<PayOutbox> findAllByStatus(OutboxStatus status);
    void deleteAllInBatch();
    List<PayOutbox> findAll();
    List<PayOutbox> findInitList(Long eventId);
}
