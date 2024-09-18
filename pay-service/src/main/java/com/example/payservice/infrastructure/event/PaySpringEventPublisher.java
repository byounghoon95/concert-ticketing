package com.example.payservice.infrastructure.event;

import com.example.payservice.domain.event.PayEventPublisher;
import com.example.payservice.domain.event.PayMessageEvent;
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