package com.example.concertticketing.interfaces.api.event;

import com.example.concertticketing.domain.message.model.OutboxDto;
import com.example.concertticketing.domain.message.service.OutboxService;
import com.example.concertticketing.domain.pay.event.PayEventPublisher;
import com.example.concertticketing.domain.pay.event.PaySendEvent;
import com.example.concertticketing.util.JsonConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PayEventListener {

    @Qualifier("PayKafkaMessagePublisher")
    private final PayEventPublisher eventPublisher;
    @Qualifier("PayOutboxService")
    private final OutboxService outboxService;
    private final JsonConverter jsonConverter;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendSlackMessage(PaySendEvent event) {
        eventPublisher.publish(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveOutbox(PaySendEvent event) {
        outboxService.save(new OutboxDto(event.payId(), jsonConverter.toJson(event)));
    }
}