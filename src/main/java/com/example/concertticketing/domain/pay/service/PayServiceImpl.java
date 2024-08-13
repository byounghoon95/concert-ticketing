package com.example.concertticketing.domain.pay.service;

import com.example.concertticketing.domain.concert.service.SeatService;
import com.example.concertticketing.domain.member.service.MemberService;
import com.example.concertticketing.domain.message.model.OutboxStatus;
import com.example.concertticketing.domain.message.repository.OutboxRepository;
import com.example.concertticketing.domain.pay.event.PaySendEvent;
import com.example.concertticketing.domain.pay.model.Pay;
import com.example.concertticketing.domain.pay.model.PayOutbox;
import com.example.concertticketing.domain.pay.repository.PayRepository;
import com.example.concertticketing.domain.queue.service.QueueService;
import com.example.concertticketing.domain.reservation.model.Reservation;
import com.example.concertticketing.domain.reservation.service.ReservationService;
import com.example.concertticketing.interfaces.api.pay.dto.PayRequest;
import com.example.concertticketing.util.JsonConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PayServiceImpl implements PayService {
    private final PayRepository payRepository;
    private final ReservationService reservationService;
    private final SeatService seatService;
    private final QueueService queueService;
    private final MemberService memberService;
    private final ApplicationEventPublisher eventPublisher;
    @Qualifier("PayOutboxRepository")
    private final OutboxRepository outboxRepository;
    private final JsonConverter jsonConverter;

    @Transactional
    @Override
    public Pay pay(PayRequest request) {
        Reservation reservation = reservationService.findById(request.reservationId());
        reservation.matchMember(reservation.getMemberId(), request.memberId());
        reservation.isAvailable();

        Pay pay = Pay.createPay(reservation);

        memberService.minusBalance(request.memberId(), reservation.getPrice());
        seatService.updateReservedAt(request.seatId(), LocalDateTime.of(9999, 12, 31, 23, 59, 59));
        queueService.expireActiveToken(request.memberId());

        Pay savedPay = payRepository.pay(pay);

        eventPublisher.publishEvent(PaySendEvent.from(savedPay));

        return savedPay;
    }

    @Transactional
    @Override
    public void republish() {
        outboxRepository.findAllByStatus(OutboxStatus.INIT).forEach(value -> {
            PayOutbox outbox = (PayOutbox) value;
            if (!outbox.isPublished()) {
                outbox.delete();
                eventPublisher.publishEvent(jsonConverter.fromJson(outbox.getPayload(), PaySendEvent.class));
            }
        });
    }
}
