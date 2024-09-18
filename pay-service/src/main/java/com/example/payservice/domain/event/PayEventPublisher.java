package com.example.payservice.domain.event;

public interface PayEventPublisher {
    void publish(PayMessageEvent event);
}
