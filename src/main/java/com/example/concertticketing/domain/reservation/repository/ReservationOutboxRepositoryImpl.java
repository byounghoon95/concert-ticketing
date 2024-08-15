package com.example.concertticketing.domain.reservation.repository;

import com.example.concertticketing.domain.message.model.OutboxDto;
import com.example.concertticketing.domain.message.model.OutboxStatus;
import com.example.concertticketing.domain.message.repository.OutboxRepository;
import com.example.concertticketing.domain.reservation.infrastructure.ReservationOutboxJpaRepository;
import com.example.concertticketing.domain.reservation.model.ReservationOutbox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ReservationOutboxRepository")
@RequiredArgsConstructor
public class ReservationOutboxRepositoryImpl implements OutboxRepository {

    private final ReservationOutboxJpaRepository outboxJpaRepository;

    @Override
    public void save(OutboxDto dto) {
        outboxJpaRepository.save(dto.toReservationOutbox());
    }

    @Override
    public List<ReservationOutbox> findAllByStatus(OutboxStatus status) {
        return outboxJpaRepository.findAllByStatus(status);
    }

    @Override
    public void deleteAllInBatch() {
        outboxJpaRepository.deleteAllInBatch();
    }

    @Override
    public List<ReservationOutbox> findAll() {
        return outboxJpaRepository.findAll();
    }

    @Override
    public List<ReservationOutbox> findInitList(Long eventId) {
        return outboxJpaRepository.findAllByEventIdAndStatus(eventId,OutboxStatus.INIT);
    }
}
