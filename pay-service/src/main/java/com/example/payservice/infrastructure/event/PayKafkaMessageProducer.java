package com.example.payservice.infrastructure.event;

import com.example.payservice.domain.event.PayEventPublisher;
import com.example.payservice.domain.event.PayMessageEvent;
import com.example.payservice.util.JsonConverter;
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
