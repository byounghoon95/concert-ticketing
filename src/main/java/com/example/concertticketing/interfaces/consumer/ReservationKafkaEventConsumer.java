package com.example.concertticketing.interfaces.consumer;

import com.example.concertticketing.domain.reservation.event.ReservationEvent;
import com.example.concertticketing.util.JsonConverter;
import com.example.concertticketing.util.SlackClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationKafkaEventConsumer {

    private final JsonConverter jsonConverter;
    private final SlackClient slackClient;

    @KafkaListener(topics = "reserve", groupId = "group_1")
    public void consume(String payload, Acknowledgment acknowledgment) {
        ReservationEvent event = jsonConverter.fromJson(payload, ReservationEvent.class);
        slackClient.sendMessage(event.payload());

        acknowledgment.acknowledge();
    }
}