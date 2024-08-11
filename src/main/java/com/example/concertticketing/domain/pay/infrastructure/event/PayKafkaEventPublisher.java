package com.example.concertticketing.domain.pay.infrastructure.event;

import com.example.concertticketing.domain.pay.event.PayEventPublisher;
import com.example.concertticketing.domain.pay.event.PaySendEvent;
import com.example.concertticketing.util.JsonConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component("PayKafkaEventPublisher")
@RequiredArgsConstructor
public class PayKafkaEventPublisher implements PayEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "pay";
    private final JsonConverter jsonConverter;

    @Override
    public void publish(PaySendEvent event) {
        String json = jsonConverter.toJson(event);
        kafkaTemplate.send(TOPIC, json);
    }
}
