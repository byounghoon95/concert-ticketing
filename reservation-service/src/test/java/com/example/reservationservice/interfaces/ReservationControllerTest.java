package com.example.reservationservice.interfaces;

import com.example.reservationservice.application.ReservationFacade;
import com.example.reservationservice.domain.clients.QueueClient;
import com.example.reservationservice.domain.model.Reservation;
import com.example.reservationservice.domain.model.ReservationStatus;
import com.example.reservationservice.domain.service.ReservationServiceImpl;
import com.example.reservationservice.interfaces.api.dto.ReserveSeatRequest;
import com.example.reservationservice.util.SlackClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationFacade reservationFacade;

    @MockBean
    private ReservationServiceImpl reservationService;

    @MockBean
    private QueueClient queueClient;

    @MockBean
    private SlackClient slackClient;

    @DisplayName("좌석을 예약한다")
    @Test
    void reserveSeat() throws Exception {
        // given
        Long memberId = 1L;
        Long seatId = 1L;
        int seatNo = 2;
        ReservationStatus status = ReservationStatus.RESERVED;

        ReserveSeatRequest request = new ReserveSeatRequest(seatId,memberId);
        Reservation reservation = Reservation.builder()
                .seatNo(seatNo)
                .status(status)
                .build();

        // when
        when(queueClient.verifyToken(memberId)).thenReturn(true);
        when(reservationFacade.reserveSeat(any(),any())).thenReturn(reservation);

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