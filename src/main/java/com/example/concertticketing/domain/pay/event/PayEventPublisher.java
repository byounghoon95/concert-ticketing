package com.example.concertticketing.domain.pay.event;

public interface PayEventPublisher {
    void publish(PaySendEvent event);
}
