package com.example.reservationservice.domain.event;

public interface ReservationEventPublisher {
    void publish(ReservationEvent event);
}
