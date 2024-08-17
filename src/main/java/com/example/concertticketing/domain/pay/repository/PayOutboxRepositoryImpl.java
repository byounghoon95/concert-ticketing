package com.example.concertticketing.domain.pay.repository;

import com.example.concertticketing.domain.message.model.OutboxDto;
import com.example.concertticketing.domain.message.model.OutboxStatus;
import com.example.concertticketing.domain.message.repository.OutboxRepository;
import com.example.concertticketing.domain.pay.infrastructure.PayOutboxJpaRepository;
import com.example.concertticketing.domain.pay.model.PayOutbox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("PayOutboxRepository")
@RequiredArgsConstructor
public class PayOutboxRepositoryImpl implements OutboxRepository {

    private final PayOutboxJpaRepository outboxJpaRepository;

    @Override
    public void save(OutboxDto dto) {
        outboxJpaRepository.save(dto.toPayOutbox());
    }

    @Override
    public List<PayOutbox> findAllByStatus(OutboxStatus status) {
        return outboxJpaRepository.findAllByStatus(status);
    }

    @Override
    public void deleteAllInBatch() {
        outboxJpaRepository.deleteAllInBatch();
    }

    @Override
    public List<PayOutbox> findAll() {
        return outboxJpaRepository.findAll();
    }

    @Override
    public List<PayOutbox> findInitList(Long eventId) {
        return outboxJpaRepository.findAllByEventIdAndStatus(eventId,OutboxStatus.INIT);
    }
}