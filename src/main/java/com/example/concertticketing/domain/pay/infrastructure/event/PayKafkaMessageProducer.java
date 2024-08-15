package com.example.concertticketing.domain.pay.infrastructure.event;

import com.example.concertticketing.domain.pay.event.PayEventPublisher;
import com.example.concertticketing.domain.pay.event.PayMessageEvent;
import com.example.concertticketing.util.JsonConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component("PayKafkaMessageProducer")
@RequiredArgsConstructor
public class PayKafkaMessageProducer implements PayEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "pay-message";
    private final JsonConverter jsonConverter;

    @Override
    public void publish(PayMessageEvent event) {
        String json = jsonConverter.toJson(event);
        kafkaTemplate.send(TOPIC, json);
    }
}
