package com.example.concertticketing.api.queue.dto;

import com.example.concertticketing.domain.queue.model.Queue;
import com.example.concertticketing.domain.queue.model.QueueStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class QueueResponse {
    UUID token;
    Long memberId;
    Long position;
    QueueStatus status;

    @Builder
    public QueueResponse(UUID token, Long memberId, Long position, QueueStatus status) {
        this.token = token;
        this.memberId = memberId;
        this.position = position;
        this.status = status;
    }

    public static QueueResponse of(Queue queue) {
        return QueueResponse.builder()
                .token(queue.getToken())
                .memberId(queue.getMember().getId())
                .position(queue.getPosition())
                .status(queue.getStatus())
                .build();
    }
}
