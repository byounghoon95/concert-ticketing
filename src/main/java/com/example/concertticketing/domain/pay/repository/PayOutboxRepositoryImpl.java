package com.example.concertticketing.domain.pay.repository;

import com.example.concertticketing.domain.message.model.OutboxDto;
import com.example.concertticketing.domain.message.model.OutboxStatus;
import com.example.concertticketing.domain.message.repository.OutboxRepository;
import com.example.concertticketing.domain.pay.infrastructure.PayOutboxJpaRepository;
import com.example.concertticketing.domain.pay.model.PayOutbox;
import com.example.concertticketing.exception.CustomException;
import com.example.concertticketing.exception.ErrorEnum;
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
    public PayOutbox findByEventId(Long eventId) {
        return outboxJpaRepository.findByEventId(eventId)
                .orElseThrow(() -> new CustomException(ErrorEnum.NO_OUTBOX));
    }

    @Override
    public List<PayOutbox> findAllByStatus(OutboxStatus status) {
        return outboxJpaRepository.findAllByStatus(status);
    }
}