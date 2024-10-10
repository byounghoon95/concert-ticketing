package com.example.reservationservice.domain;

import com.example.reservationservice.domain.clients.SeatClient;
import com.example.reservationservice.domain.external.SeatResponse;
import com.example.reservationservice.domain.model.Reservation;
import com.example.reservationservice.domain.model.ReservationStatus;
import com.example.reservationservice.domain.repository.ReservationRepository;
import com.example.reservationservice.domain.service.ReservationServiceImpl;
import com.example.reservationservice.interfaces.api.common.response.CommonResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private SeatClient seatClient;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @DisplayName("좌석을 예약한다")
    @Test
    void reserveSeat() {
        // given
        Long seatId = 1L;
        Long memberId = 1L;
        Long concertDetailId = 1L;
        int seatNo = 1;
        Long price = 5000L;
        SeatResponse seat = new SeatResponse(seatId, memberId, concertDetailId, seatNo, price, LocalDateTime.now());

        // when
        when(seatClient.findById(any())).thenReturn(CommonResponse.success(seat));
        when(reservationRepository.reserveSeat(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Reservation response = reservationService.reserveSeat(seatId,memberId,null);

        // then
        assertThat(response.getStatus()).isEqualTo(ReservationStatus.RESERVED);
    }
}