package com.example.concertticketing.domain.pay.service;

import com.example.concertticketing.api.pay.dto.PayRequest;
import com.example.concertticketing.domain.concert.service.SeatService;
import com.example.concertticketing.domain.member.service.MemberService;
import com.example.concertticketing.domain.pay.model.Pay;
import com.example.concertticketing.domain.pay.repository.PayRepository;
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
    private final MemberService memberService;

    @Transactional
    @Override
    public Pay pay(PayRequest request) {
        Reservation reservation = reservationService.findById(request.reservationId());
        reservation.matchMember(reservation.getMemberId(), request.memberId());
        reservation.isAvailable();

        Pay pay = Pay.createPay(reservation);

        memberService.minusBalance(request.memberId(), reservation.getPrice());
        seatService.updateReservedAt(request.seatId(), LocalDateTime.of(9999, 12, 31, 23, 59, 59));
        queueService.expiredToken(request.memberId());

        return payRepository.pay(pay);
    }
}
