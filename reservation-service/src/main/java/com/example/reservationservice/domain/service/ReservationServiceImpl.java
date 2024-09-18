package com.example.reservationservice.domain.service;

import com.example.reservationservice.domain.event.ReservationEvent;
import com.example.reservationservice.domain.external.SeatCompensation;
import com.example.reservationservice.domain.message.model.OutboxStatus;
import com.example.reservationservice.domain.message.repository.OutboxRepository;
import com.example.reservationservice.domain.model.Reservation;
import com.example.reservationservice.domain.model.ReservationOutbox;
import com.example.reservationservice.domain.repository.ReservationRepository;
import com.example.reservationservice.util.JsonConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
//    private final SeatRepository seatRepository;
    @Qualifier("ReservationOutboxRepository")
    private final OutboxRepository outboxRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final JsonConverter jsonConverter;

    @Transactional
    @Override
    public Reservation reserveSeat(Long seatId, Long memberId, SeatCompensation seatComp) {
//        Seat seat = seatRepository.findById(seatId)
//                .orElseThrow(() -> new CustomException(ErrorEnum.NO_SEAT));

//        Reservation reservation = Reservation.createReservation(seat, memberId);
//        Reservation savedReservation = reservationRepository.reserveSeat(reservation);

//        eventPublisher.publishEvent(ReservationEvent.from(savedReservation,seatComp));

        return Reservation.createReservation(memberId);
//        return savedReservation;
    }

    @Override
    public Reservation findById(Long reservationId) {
        return reservationRepository.findById(reservationId);
    }

    @Transactional
    @Override
    public void republish() {
        outboxRepository.findAllByStatus(OutboxStatus.INIT).forEach(value -> {
            ReservationOutbox outbox = (ReservationOutbox) value;
            if (!outbox.isPublished()) {
                outbox.published();
                eventPublisher.publishEvent(jsonConverter.fromJson(outbox.getPayload(), ReservationEvent.class));
            }
        });
    }
}
