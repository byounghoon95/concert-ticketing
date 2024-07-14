package com.example.concertticketing.api.pay;

import com.example.concertticketing.CommonControllerTest;
import com.example.concertticketing.api.pay.dto.PayRequest;
import com.example.concertticketing.domain.pay.model.Pay;
import com.example.concertticketing.domain.reservation.model.Reservation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PayControllerTest extends CommonControllerTest {

    @DisplayName("예약 번호를 받아 해당 예약을 결제한다")
    @Test
    void pay() throws Exception {
        // given
        Long reservationId = 1L;
        Long memberId = 1L;
        Long seatId = 1L;
        int seatNo = 2;
        Long amount = 5000L;

        PayRequest request = new PayRequest(reservationId,memberId,seatId);
        Reservation reservation = Reservation.builder()
                .seatNo(seatNo)
                .build();

        Pay pay = Pay.builder()
                .reservation(reservation)
                .amount(amount)
                .build();

        // when
        when(queueService.verify(memberId)).thenReturn(true);
        when(payService.pay(any())).thenReturn(pay);

        // then
        mockMvc.perform(post("/api/pay")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("memberId",memberId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.amount").value(amount))
                .andExpect(jsonPath("$.data.seat").value(seatNo))
        ;
    }
}