package com.example.payservice.domain.repository;

import com.example.payservice.domain.message.model.OutboxDto;
import com.example.payservice.domain.message.model.OutboxStatus;
import com.example.payservice.domain.model.PayOutbox;
import com.example.payservice.infrastructure.PayOutboxJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PayOutboxRepositoryImpl implements PayOutboxRepository {

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