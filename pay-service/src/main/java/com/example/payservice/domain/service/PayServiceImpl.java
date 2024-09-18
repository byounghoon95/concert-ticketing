package com.example.payservice.domain.service;

import com.example.payservice.domain.event.PayMessageEvent;
import com.example.payservice.domain.message.model.OutboxStatus;
import com.example.payservice.domain.message.repository.OutboxRepository;
import com.example.payservice.domain.model.Pay;
import com.example.payservice.domain.model.PayOutbox;
import com.example.payservice.domain.repository.PayRepository;
import com.example.payservice.interfaces.api.dto.PayRequest;
import com.example.payservice.util.JsonConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PayServiceImpl implements PayService {
    private final PayRepository payRepository;
//    private final ReservationService reservationService;
//    private final SeatService seatService;
//    private final QueueService queueService;
//    private final MemberService memberService;
    private final ApplicationEventPublisher eventPublisher;
    @Qualifier("PayOutboxRepository")
    private final OutboxRepository outboxRepository;
    private final JsonConverter jsonConverter;

    @Transactional
    @Override
    public Pay pay(PayRequest request) {
//        Reservation reservation = reservationService.findById(request.reservationId());
//        reservation.matchMember(reservation.getMemberId(), request.memberId());
//        reservation.isAvailable();

        Pay pay = Pay.createPay();
//        Pay pay = Pay.createPay(reservation);

//        memberService.minusBalance(request.memberId(), reservation.getPrice());
//        seatService.updateReservedAt(request.seatId(), LocalDateTime.of(9998, 12, 31, 23, 59, 59));
//        queueService.expireActiveToken(request.memberId());

        Pay savedPay = payRepository.pay(pay);

        eventPublisher.publishEvent(PayMessageEvent.from(savedPay));

        return savedPay;
    }

    @Transactional
    @Override
    public void republish() {
        outboxRepository.findAllByStatus(OutboxStatus.INIT).forEach(value -> {
            PayOutbox outbox = (PayOutbox) value;
            if (!outbox.isPublished()) {
                outbox.published();
//                eventPublisher.publishEvent(jsonConverter.fromJson(outbox.getPayload(), ReservationEvent.class));
            }
        });
    }
}
