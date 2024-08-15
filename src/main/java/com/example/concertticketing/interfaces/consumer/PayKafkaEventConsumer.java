package com.example.concertticketing.interfaces.consumer;

import com.example.concertticketing.domain.message.service.OutboxService;
import com.example.concertticketing.domain.pay.event.PayMessageEvent;
import com.example.concertticketing.util.JsonConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayKafkaEventConsumer {

    private final JsonConverter jsonConverter;
    @Qualifier("PayOutboxService")
    private final OutboxService outboxService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @KafkaListener(topics = "pay-message", groupId = "group_1")
    public void consume(String payload, Acknowledgment acknowledgment) {
        PayMessageEvent event = jsonConverter.fromJson(payload, PayMessageEvent.class);
        outboxService.published(event.payId());

        acknowledgment.acknowledge();
    }
}