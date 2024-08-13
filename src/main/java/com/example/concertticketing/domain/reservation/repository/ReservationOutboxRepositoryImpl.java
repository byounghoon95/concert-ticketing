package com.example.concertticketing.domain.reservation.repository;

import com.example.concertticketing.domain.message.model.OutboxDto;
import com.example.concertticketing.domain.message.model.OutboxStatus;
import com.example.concertticketing.domain.message.repository.OutboxRepository;
import com.example.concertticketing.domain.reservation.infrastructure.ReservationOutboxJpaRepository;
import com.example.concertticketing.domain.reservation.model.ReservationOutbox;
import com.example.concertticketing.exception.CustomException;
import com.example.concertticketing.exception.ErrorEnum;
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
    public ReservationOutbox findByEventId(Long eventId) {
        return outboxJpaRepository.findByEventId(eventId)
                .orElseThrow(() -> new CustomException(ErrorEnum.NO_OUTBOX));
    }

    @Override
    public List<ReservationOutbox> findAllByStatus(OutboxStatus status) {
        return outboxJpaRepository.findAllByStatus(status);
    }
}
