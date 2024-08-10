package com.example.concertticketing.domain.pay.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaySpringEventPublisher implements PayEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(PaySendEvent event) {
        eventPublisher.publishEvent(event);
    }
}
