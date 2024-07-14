package com.example.concertticketing.domain.queue.component;

import com.example.concertticketing.domain.queue.model.QueueStatus;
import com.example.concertticketing.domain.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

// TODO 60000 이 1분, 추 후 변경 필요
@RequiredArgsConstructor
@Component
public class ExpireScheduler {

    private final QueueService queueService;

    /**
     * 활성화된 큐를 만료
     * */
    @Scheduled(fixedDelay = 10000)
    public void processExpiredQueues() {
        queueService.updateActiveTokenToExpired(LocalDateTime.now(), QueueStatus.ACTIVE, QueueStatus.EXPIRED);
    }

    /**
     * 대기중인 큐를 활성화
     * */
    @Scheduled(fixedDelay = 10000)
    public void processActiveToken() {
        queueService.updateWaitTokenToActive(LocalDateTime.now(), QueueStatus.WAIT, QueueStatus.ACTIVE);
    }
}
