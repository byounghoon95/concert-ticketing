package com.example.concertticketing.interfaces.consumer;

import com.example.concertticketing.domain.message.service.OutboxService;
import com.example.concertticketing.domain.reservation.event.ReservationEvent;
import com.example.concertticketing.util.JsonConverter;
import com.example.concertticketing.util.SlackClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationKafkaEventConsumer {

    private final JsonConverter jsonConverter;
    @Qualifier("ReservationOutboxService")
    private final OutboxService outboxService;
    private final SlackClient slackClient;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @KafkaListener(topics = "reserve-message", groupId = "reservation-message")
    public void sendMessage(String payload, Acknowledgment acknowledgment) {
        ReservationEvent event = jsonConverter.fromJson(payload, ReservationEvent.class);
        slackClient.sendMessage(event.payload());

        acknowledgment.acknowledge();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @KafkaListener(topics = "reserve-message", groupId = "reservation-outbox")
    public void consume(String payload, Acknowledgment acknowledgment) {
        ReservationEvent event = jsonConverter.fromJson(payload, ReservationEvent.class);
        outboxService.published(event.reservationId());

        acknowledgment.acknowledge();
    }
}