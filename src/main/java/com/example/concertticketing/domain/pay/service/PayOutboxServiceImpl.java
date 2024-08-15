package com.example.concertticketing.domain.pay.service;

import com.example.concertticketing.domain.message.model.OutboxDto;
import com.example.concertticketing.domain.message.repository.OutboxRepository;
import com.example.concertticketing.domain.message.service.OutboxService;
import com.example.concertticketing.domain.pay.model.PayOutbox;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    /**
     * 마킹을 하고 찾기에 List 로 반환될 일 없음
     * 하지만, 로직이 꼬여 중복으로 값이 들어갈 경우를 막기 위함
     * */
    @Override
    public void published(Long eventId) {
        List<PayOutbox> list = outboxRepository.findInitList(eventId);
        list.forEach(outbox -> outbox.published());
    }
}
