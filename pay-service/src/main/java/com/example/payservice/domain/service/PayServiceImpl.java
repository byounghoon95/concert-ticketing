package com.example.payservice.domain.service;

import com.example.payservice.domain.clients.MemberClient;
import com.example.payservice.domain.clients.QueueClient;
import com.example.payservice.domain.clients.ReservationClient;
import com.example.payservice.domain.clients.SeatClient;
import com.example.payservice.domain.event.PayMessageEvent;
import com.example.payservice.domain.message.model.OutboxStatus;
import com.example.payservice.domain.model.Pay;
import com.example.payservice.domain.repository.PayOutboxRepository;
import com.example.payservice.domain.repository.PayRepository;
import com.example.payservice.external.MemberChargeRequest;
import com.example.payservice.external.ReservePayResponse;
import com.example.payservice.interfaces.api.dto.PayRequest;
import com.example.payservice.util.JsonConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PayServiceImpl implements PayService {
    private final PayRepository payRepository;
    private final ReservationClient reservationClient;
    private final SeatClient seatClient;
    private final QueueClient queueClient;
    private final MemberClient memberClient;

    private final ApplicationEventPublisher eventPublisher;
    private final PayOutboxRepository outboxRepository;
    private final JsonConverter jsonConverter;

    @Transactional
    @Override
    public Pay pay(PayRequest request) {
        ReservePayResponse reservation = reservationClient.verifyReservation(request.reservationId(), request.memberId()).getData();

        Pay pay = Pay.createPay(reservation);

        memberClient.chargeBalance(new MemberChargeRequest(request.memberId(), -reservation.price()));
        seatClient.confirmSeat(request.seatId());
        queueClient.expireActiveToken(request.memberId());

        Pay savedPay = payRepository.pay(pay);

        eventPublisher.publishEvent(PayMessageEvent.from(savedPay));

        return savedPay;
    }

    @Transactional
    @Override
    public void republish() {
        outboxRepository.findAllByStatus(OutboxStatus.INIT).forEach(outbox -> {
            if (!outbox.isPublished()) {
                outbox.published();
                eventPublisher.publishEvent(jsonConverter.fromJson(outbox.getPayload(), PayMessageEvent.class));
            }
        });
    }
}
