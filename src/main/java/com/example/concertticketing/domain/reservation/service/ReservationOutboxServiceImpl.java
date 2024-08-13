package com.example.concertticketing.domain.reservation.service;

import com.example.concertticketing.domain.message.model.OutboxDto;
import com.example.concertticketing.domain.message.repository.OutboxRepository;
import com.example.concertticketing.domain.message.service.OutboxService;
import com.example.concertticketing.domain.reservation.model.ReservationOutbox;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("ReservationOutboxService")
@RequiredArgsConstructor
public class ReservationOutboxServiceImpl implements OutboxService {

    @Qualifier("ReservationOutboxRepository")
    private final OutboxRepository outboxRepository;

    @Override
    @Transactional
    public void save(OutboxDto dto) {
        outboxRepository.save(dto);
    }

    @Override
    public void published(Long eventId) {
        ReservationOutbox outbox = (ReservationOutbox) outboxRepository.findByEventId(eventId);
        outbox.published();
    }
}
