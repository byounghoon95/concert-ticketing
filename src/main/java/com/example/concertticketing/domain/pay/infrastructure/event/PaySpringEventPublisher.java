package com.example.concertticketing.domain.pay.infrastructure.event;

import com.example.concertticketing.domain.pay.event.PayEventPublisher;
import com.example.concertticketing.domain.pay.event.PayMessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component("PaySpringEventPublisher")
@RequiredArgsConstructor
public class PaySpringEventPublisher implements PayEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(PayMessageEvent event) {
        eventPublisher.publishEvent(event);
    }
}