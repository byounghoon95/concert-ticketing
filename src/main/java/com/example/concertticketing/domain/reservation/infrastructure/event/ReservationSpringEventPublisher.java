package com.example.concertticketing.domain.reservation.infrastructure.event;

import com.example.concertticketing.domain.reservation.event.ReservationEvent;
import com.example.concertticketing.domain.reservation.event.ReservationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component("ReservationSpringEventPublisher")
@RequiredArgsConstructor
public class ReservationSpringEventPublisher implements ReservationEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(ReservationEvent event) {
        eventPublisher.publishEvent(event);
    }
}
