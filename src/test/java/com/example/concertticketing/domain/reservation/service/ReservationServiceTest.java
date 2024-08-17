package com.example.concertticketing.domain.reservation.service;

import com.example.concertticketing.domain.concert.model.ConcertDetail;
import com.example.concertticketing.domain.concert.model.Seat;
import com.example.concertticketing.domain.concert.repository.SeatRepository;
import com.example.concertticketing.domain.member.model.Member;
import com.example.concertticketing.domain.reservation.model.Reservation;
import com.example.concertticketing.domain.reservation.model.ReservationStatus;
import com.example.concertticketing.domain.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @DisplayName("좌석을 예약한다")
    @Test
    void reserveSeat() {
        // given
        Long seatId = 1L;
        Long memberId = 1L;
        Member member = Member.builder()
                .id(memberId)
                .build();
        ConcertDetail concert = ConcertDetail.builder()
                .name("A1")
                .build();
        Seat seat = Seat.builder()
                .member(member)
                .concert(concert)
                .build();

        // when
        when(seatRepository.findById(any())).thenReturn(Optional.of(seat));
        when(reservationRepository.reserveSeat(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reservation response = reservationService.reserveSeat(seatId,memberId,null);

        // then
        assertThat(response.getStatus()).isEqualTo(ReservationStatus.RESERVED);
        assertThat(response.getConcertName()).isEqualTo(concert.getName());
    }
}