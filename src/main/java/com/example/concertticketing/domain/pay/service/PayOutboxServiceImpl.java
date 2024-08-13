package com.example.concertticketing.domain.pay.service;

import com.example.concertticketing.domain.message.model.OutboxDto;
import com.example.concertticketing.domain.message.repository.OutboxRepository;
import com.example.concertticketing.domain.message.service.OutboxService;
import com.example.concertticketing.domain.pay.model.PayOutbox;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("PayOutboxService")
@RequiredArgsConstructor
public class PayOutboxServiceImpl implements OutboxService {

    @Qualifier("PayOutboxRepository")
    private final OutboxRepository outboxRepository;

    @Override
    @Transactional
    public void save(OutboxDto dto) {
        outboxRepository.save(dto);
    }

    @Override
    public void published(Long eventId) {
        PayOutbox outbox = (PayOutbox) outboxRepository.findByEventId(eventId);
        outbox.published();
    }
}
