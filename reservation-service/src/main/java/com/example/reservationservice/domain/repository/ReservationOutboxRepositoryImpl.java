package com.example.reservationservice.domain.repository;

import com.example.reservationservice.domain.message.model.OutboxDto;
import com.example.reservationservice.domain.message.model.OutboxStatus;
import com.example.reservationservice.domain.message.repository.OutboxRepository;
import com.example.reservationservice.domain.model.ReservationOutbox;
import com.example.reservationservice.infrastructure.ReservationOutboxJpaRepository;
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
