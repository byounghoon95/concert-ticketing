package com.example.concertticketing.api.concert;

import com.example.concertticketing.CommonControllerTest;
import com.example.concertticketing.domain.concert.model.ConcertDate;
import com.example.concertticketing.domain.concert.model.ConcertDateDetails;
import com.example.concertticketing.domain.concert.model.ConcertSeat;
import com.example.concertticketing.domain.concert.model.ConcertSeatDetail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ConcertControllerTest extends CommonControllerTest {

    @DisplayName("예약 가능한 날짜 목록을 조회한다")
    @Test
    void getAvailableDates() throws Exception {
        // given
        Long concertId = 1L;
        Long memberId = 1L;
        LocalDateTime today = LocalDateTime.now();
        List<ConcertDateDetails> details = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            details.add(new ConcertDateDetails(concertId + i, today.plusDays(i)));
        }

        ConcertDate response = new ConcertDate(concertId, details);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        // when
        when(queueService.verify(memberId)).thenReturn(true);
        when(concertService.selectAvailableDates(any())).thenReturn(response);

        // then
        mockMvc.perform(get("/api/concert/date/{concertId}", concertId)
                        .header("memberId",memberId)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.concertId").value(concertId))
                .andExpect(jsonPath("$.data.concertDates[0].dates").value(today.plusDays(0).format(formatter)))
                .andExpect(jsonPath("$.data.concertDates[0].concertDetailId").value(1L))
                .andExpect(jsonPath("$.data.concertDates[1].dates").value(today.plusDays(1).format(formatter)))
                .andExpect(jsonPath("$.data.concertDates[1].concertDetailId").value(2L))
        ;
    }

    @DisplayName("예약 가능한 좌석 목록을 조회한다")
    @Test
    void getAvailableSeats() throws Exception {
        // given
        Long memberId = 1L;
        Long concertDetailId = 1L;
        Long seatId = 1L;
        int seatNo = 1;
        List<ConcertSeatDetail> details = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            details.add(new ConcertSeatDetail(seatId + i, seatNo + i));
        }

        ConcertSeat response = new ConcertSeat(concertDetailId, details);

        // when
        when(queueService.verify(memberId)).thenReturn(true);
        when(concertService.selectAvailableSeats(any())).thenReturn(response);

        // then
        mockMvc.perform(get("/api/concert/seat/{concertId}", concertDetailId)
                        .header("memberId",memberId)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.concertDetailId").value(concertDetailId))
                .andExpect(jsonPath("$.data.concertSeats[0].seatId").value(1L))
                .andExpect(jsonPath("$.data.concertSeats[0].seatNo").value(1))
                .andExpect(jsonPath("$.data.concertSeats[1].seatId").value(2L))
                .andExpect(jsonPath("$.data.concertSeats[1].seatNo").value(2))
        ;
    }
}