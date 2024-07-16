package com.example.concertticketing.api.reservation;

import com.example.concertticketing.CommonControllerTest;
import com.example.concertticketing.api.reservation.dto.ReserveSeatRequest;
import com.example.concertticketing.domain.reservation.model.Reservation;
import com.example.concertticketing.domain.reservation.model.ReservationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReservationControllerTest extends CommonControllerTest {

    @DisplayName("좌석을 예약한다")
    @Test
    void reserveSeat() throws Exception {
        // given
        Long memberId = 1L;
        Long seatId = 1L;
        int seatNo = 2;
        ReservationStatus status = ReservationStatus.RESERVED;

        ReserveSeatRequest request = new ReserveSeatRequest(seatId);
        Reservation reservation = Reservation.builder()
                .seatNo(seatNo)
                .status(status)
                .build();

        // when
        when(queueService.verify(memberId)).thenReturn(true);
        when(reservationService.reserveSeat(any())).thenReturn(reservation);

        // then
        mockMvc.perform(post("/api/reserve")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("memberId",memberId)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.seatNo").value(seatNo))
                .andExpect(jsonPath("$.data.status").value(status.toString()))
        ;
    }
}