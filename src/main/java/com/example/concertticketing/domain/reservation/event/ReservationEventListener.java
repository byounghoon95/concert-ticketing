package com.example.concertticketing.domain.reservation.event;

import com.example.concertticketing.domain.util.SlackClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReservationEventListener {
    private final SlackClient slackClient;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handler(ReservationEvent event) {
        slackClient.sendMessage(event.payload());
    }
}
