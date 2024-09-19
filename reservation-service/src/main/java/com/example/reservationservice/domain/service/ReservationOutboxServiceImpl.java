package com.example.reservationservice.domain.service;

import com.example.reservationservice.domain.model.OutboxDto;
import com.example.reservationservice.domain.model.ReservationOutbox;
import com.example.reservationservice.domain.repository.ReservationOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationOutboxServiceImpl implements ReservationOutboxService {

    private final ReservationOutboxRepository outboxRepository;

    @Override
    @Transactional
    public void save(OutboxDto dto) {
        outboxRepository.save(dto);
    }

    /**
     * 마킹을 하고 찾기에 List 로 반환될 일 없음
     * 하지만, 로직이 꼬여 중복으로 값이 들어갈 경우를 막기 위함
     * */
    @Override
    public void published(Long eventId) {
        List<ReservationOutbox> list = outboxRepository.findInitList(eventId);
        list.forEach(outbox -> outbox.published());
    }
}
