package com.example.concertticketing.domain.queue.model;

import java.util.UUID;

public record QueueInfo(
        UUID token,
        Long memberId,
        Long position
) {
}
