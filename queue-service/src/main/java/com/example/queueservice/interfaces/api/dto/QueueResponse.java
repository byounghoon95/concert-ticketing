package com.example.queueservice.interfaces.api.dto;

import com.example.queueservice.domain.model.Queue;
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
