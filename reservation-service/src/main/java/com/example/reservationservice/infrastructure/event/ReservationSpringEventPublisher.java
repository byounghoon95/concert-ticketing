package com.example.reservationservice.infrastructure.event;

import com.example.reservationservice.domain.event.ReservationEvent;
import com.example.reservationservice.domain.event.ReservationEventPublisher;
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
