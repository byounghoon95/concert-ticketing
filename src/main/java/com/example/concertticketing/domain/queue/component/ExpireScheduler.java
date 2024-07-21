package com.example.concertticketing.domain.queue.component;

import com.example.concertticketing.domain.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExpireScheduler {

    private final QueueService queueService;

    /**
     * 활성화된 큐를 만료
     * */
    @Scheduled(fixedDelay = 60000) // 1분
    public void processExpiredQueues() {
        long start = System.currentTimeMillis();
        queueService.updateActiveTokenToExpired(LocalDateTime.now());
        long end = System.currentTimeMillis();
        log.info("대기열 만료 API 소요시간 : {} ms", (end - start));
    }

    /**
     * 대기중인 큐를 활성화
     * */
    @Scheduled(fixedDelay = 60000)
    public void processActiveToken() {
        long start = System.currentTimeMillis();
        queueService.updateWaitTokenToActive(LocalDateTime.now(), 10);
        long end = System.currentTimeMillis();
        log.info("대기열 활성화 API 소요시간 : {} ms", (end - start));
    }
}
