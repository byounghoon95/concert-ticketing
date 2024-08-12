package com.example.concertticketing.interfaces.api.event;

import com.example.concertticketing.domain.concert.service.SeatService;
import com.example.concertticketing.domain.reservation.event.ReservationEvent;
import com.example.concertticketing.domain.reservation.event.ReservationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReservationEventListener {

    @Qualifier("ReservationKafkaEventPublisher")
    private final ReservationEventPublisher eventPublisher;
    private final SeatService seatService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendSlackMessage(ReservationEvent event) {
        eventPublisher.publish(event);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void rollback(ReservationEvent event) {
        seatService.rollbackSeat(event.seat());
    }
}
