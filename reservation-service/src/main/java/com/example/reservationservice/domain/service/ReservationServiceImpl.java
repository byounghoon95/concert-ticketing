package com.example.reservationservice.domain.service;

import com.example.reservationservice.domain.clients.SeatClient;
import com.example.reservationservice.domain.event.ReservationEvent;
import com.example.reservationservice.domain.external.SeatCompensation;
import com.example.reservationservice.domain.external.SeatResponse;
import com.example.reservationservice.domain.model.OutboxStatus;
import com.example.reservationservice.domain.model.Reservation;
import com.example.reservationservice.domain.repository.ReservationOutboxRepository;
import com.example.reservationservice.domain.repository.ReservationRepository;
import com.example.reservationservice.util.JsonConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatClient seatClient;
    private final ReservationOutboxRepository outboxRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final JsonConverter jsonConverter;

    @Transactional
    @Override
    public Reservation reserveSeat(Long seatId, Long memberId, SeatCompensation seatComp) {
        SeatResponse seat = seatClient.findById(seatId).getData();

        Reservation reservation = Reservation.createReservation(seat, memberId);
        Reservation savedReservation = reservationRepository.reserveSeat(reservation);

        eventPublisher.publishEvent(ReservationEvent.from(savedReservation,seatComp));

        return savedReservation;
    }

    @Override
    public Reservation findById(Long reservationId) {
        return reservationRepository.findById(reservationId);
    }

    @Transactional
    @Override
    public void republish() {
        outboxRepository.findAllByStatus(OutboxStatus.INIT).forEach(outbox -> {
            if (!outbox.isPublished()) {
                outbox.published();
                eventPublisher.publishEvent(jsonConverter.fromJson(outbox.getPayload(), ReservationEvent.class));
            }
        });
    }

    @Override
    public Reservation verifyReservation(Long reservationId, Long memberId) {
        Reservation reservation = reservationRepository.findById(reservationId);
        reservation.matchMember(reservation.getMemberId(), memberId);
        reservation.isAvailable();
        return reservation;
    }
}
