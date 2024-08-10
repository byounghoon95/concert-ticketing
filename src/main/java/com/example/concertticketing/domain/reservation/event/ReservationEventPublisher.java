package com.example.concertticketing.domain.reservation.event;

public interface ReservationEventPublisher {
    void publish(ReservationEvent event);
}
