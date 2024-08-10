package com.example.concertticketing.domain.reservation.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationSpringEventPublisher implements ReservationEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(ReservationEvent event) {
        eventPublisher.publishEvent(event);
    }
}
