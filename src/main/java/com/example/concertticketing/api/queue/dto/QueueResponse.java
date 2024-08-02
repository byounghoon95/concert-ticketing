package com.example.concertticketing.api.queue.dto;

import com.example.concertticketing.domain.queue.model.Queue;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QueueResponse {
    Long memberId;
    Long position;

    @Builder
    public QueueResponse(Long memberId, Long position) {
        this.memberId = memberId;
        this.position = position;
    }

    public static QueueResponse of(Queue queue) {
        return QueueResponse.builder()
                .memberId(queue.getMemberId())
                .position(queue.getPosition())
                .build();
    }
}
