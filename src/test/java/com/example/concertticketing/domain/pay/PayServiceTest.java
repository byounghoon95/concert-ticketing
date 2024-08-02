package com.example.concertticketing.domain.pay;

import com.example.concertticketing.api.pay.dto.PayRequest;
import com.example.concertticketing.domain.concert.service.SeatServiceImpl;
import com.example.concertticketing.domain.member.service.MemberServiceImpl;
import com.example.concertticketing.domain.pay.model.Pay;
import com.example.concertticketing.domain.pay.model.PayStatus;
import com.example.concertticketing.domain.pay.repository.PayRepository;
import com.example.concertticketing.domain.pay.service.PayServiceImpl;
import com.example.concertticketing.domain.queue.service.QueueServiceImpl;
import com.example.concertticketing.domain.reservation.model.Reservation;
import com.example.concertticketing.domain.reservation.service.ReservationServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class PayServiceTest {
    @Mock
    private PayRepository payRepository;

    @Mock
    private ReservationServiceImpl reservationService;

    @Mock
    private QueueServiceImpl queueService;

    @Mock
    private SeatServiceImpl seatService;

    @Mock
    private MemberServiceImpl memberService;

    @InjectMocks
    private PayServiceImpl payService;

    @DisplayName("예약 번호를 받아 결제한다")
    @Test
    void pay() {
        // given
        Long reservationId = 1L;
        Long memberId = 1L;
        Long seatId = 1L;
        Long price = 5000L;
        Long amount = 2000L;

        PayRequest request = new PayRequest(reservationId,memberId,seatId);

        Reservation reservation = Reservation.builder()
                .memberId(memberId)
                .price(price)
                .createdAt(LocalDateTime.now())
                .build();

        // when
        when(reservationService.findById(any())).thenReturn(reservation);
        when(payRepository.pay(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Pay response = payService.pay(request);

        // then
        assertThat(response.getAmount()).isEqualTo(price);
        assertThat(response.getStatus()).isEqualTo(PayStatus.PAYED);
    }
}