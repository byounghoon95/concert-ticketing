package com.example.reservationservice.infrastructure.event;

import com.example.reservationservice.domain.event.ReservationEvent;
import com.example.reservationservice.domain.event.ReservationEventPublisher;
import com.example.reservationservice.util.JsonConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component("ReservationKafkaMessageProducer")
@RequiredArgsConstructor
public class ReservationKafkaMessageProducer implements ReservationEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "reserve-message";
    private final JsonConverter jsonConverter;

    @Override
    public void publish(ReservationEvent event) {
        String json = jsonConverter.toJson(event);
        kafkaTemplate.send(TOPIC, json);
    }
}