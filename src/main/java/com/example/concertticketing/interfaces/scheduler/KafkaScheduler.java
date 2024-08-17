package com.example.concertticketing.interfaces.scheduler;

import com.example.concertticketing.domain.pay.service.PayService;
import com.example.concertticketing.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaScheduler {

    private final ReservationService reservationService;
    private final PayService payService;

    @Scheduled(fixedDelay = 60000)
    public void publishReservation() {
        long start = System.currentTimeMillis();
        reservationService.republish();
        long end = System.currentTimeMillis();
        log.info("예약 카프카 메세지 재발행 API 소요시간 : {} ms", (end - start));
    }

    @Scheduled(fixedDelay = 60000)
    public void publishPay() {
        long start = System.currentTimeMillis();
        payService.republish();
        long end = System.currentTimeMillis();
        log.info("결제 카프카 메세지 재발행 API 소요시간 : {} ms", (end - start));
    }
}
