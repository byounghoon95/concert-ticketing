package com.example.concertticketing.interfaces.api.event;

import com.example.concertticketing.domain.pay.event.PaySendEvent;
import com.example.concertticketing.util.SlackClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PayEventListener {
    private final SlackClient slackClient;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handler(PaySendEvent event) {
        slackClient.sendMessage(event.payload());
    }
}
