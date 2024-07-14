package com.example.concertticketing.domain.pay.service;

import com.example.concertticketing.api.pay.dto.PayRequest;
import com.example.concertticketing.domain.concert.service.SeatService;
import com.example.concertticketing.domain.pay.model.Pay;
import com.example.concertticketing.domain.pay.model.PayStatus;
import com.example.concertticketing.domain.pay.repository.PayRepository;
import com.example.concertticketing.domain.queue.model.QueueStatus;
import com.example.concertticketing.domain.queue.service.QueueService;
import com.example.concertticketing.domain.reservation.model.Reservation;
import com.example.concertticketing.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PayServiceImpl implements PayService {
    private final PayRepository payRepository;
    private final ReservationService reservationService;
    private final QueueService queueService;
    private final SeatService seatService;

    @Transactional
    @Override
    public Pay pay(PayRequest request) {
        Reservation reservation = reservationService.findById(request.reservationId());
        if (reservation.getMemberId() != request.memberId()) {
            throw new IllegalStateException("멤버 정보와 예약 정보가 일치하지 않습니다");
        }

        Pay pay = Pay.builder()
                .reservation(reservation)
                .amount(reservation.getPrice())
                .status(PayStatus.PAYED)
                .build();

        queueService.expiredToken(request.memberId(), QueueStatus.EXPIRED);
        seatService.updateReservedAt(request.seatId(), LocalDateTime.of(9999, 12, 31, 23, 59, 59));

        return payRepository.pay(pay);
    }
}
