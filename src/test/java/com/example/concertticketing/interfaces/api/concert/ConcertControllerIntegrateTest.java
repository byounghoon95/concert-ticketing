package com.example.concertticketing.interfaces.api.concert;

import com.example.concertticketing.CommonControllerIntegrateTest;
import com.example.concertticketing.interfaces.api.common.response.CommonResponse;
import com.example.concertticketing.domain.concert.model.ConcertDate;
import com.example.concertticketing.domain.concert.model.ConcertDateDetails;
import com.example.concertticketing.domain.concert.model.ConcertSeat;
import com.example.concertticketing.domain.concert.model.ConcertSeatDetail;
import com.example.concertticketing.exception.ErrorEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ConcertControllerIntegrateTest extends CommonControllerIntegrateTest {
    @AfterEach
    void tearDown() {
        queueRepository.flushAll();
        concertRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("예약 가능한 날짜 목록을 조회한다")
    @Test
    void getAvailableDates() {
        // given
        setUpConcert();
        Long concertId = findFirstConcertId();
        Long concertDetailId = findFirstConcertDetailId();
        Long memberId = findFirstMemberId();
        String url = "http://localhost:" + port + "/api/concert/date/" + concertId;

        // HttpEntity 에 헤더 포함
        HttpEntity<String> header = setHeader(memberId);

        // when
        ResponseEntity<CommonResponse<ConcertDate>> response = restTemplate.exchange(url, HttpMethod.GET, header, new ParameterizedTypeReference<>() {});
        CommonResponse<ConcertDate> body = response.getBody();
        ConcertDate concertDate = body.getData();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.SUCCESS.getMessage());
        assertThat(concertDate.concertId()).isEqualTo(concertId);
        assertThat(concertDate.concertDates().size()).isEqualTo(3);
        assertThat(concertDate.concertDates().get(0)).isInstanceOf(ConcertDateDetails.class);
        assertThat(concertDate.concertDates().get(1).concertDetailId()).isEqualTo(concertDetailId + 1);
        assertThat(concertDate.concertDates().get(1).dates()).isEqualTo(LocalDateTime.of(2024, 6, 12, 0, 0, 0).plusDays(1));
    }

    @DisplayName("예약 가능한 날짜 목록 조회 시 토큰이 없어 실패한다")
    @Test
    void getAvailableDates_fail_no_token() {
        // given
        setUpConcert();
        Long concertId = findFirstConcertId();
        Long memberId = findFirstMemberId() + 1;
        String url = "http://localhost:" + port + "/api/concert/date/" + concertId;

        // HttpEntity 에 헤더 포함
        HttpEntity<String> header = setHeader(memberId);

        // when
        ResponseEntity<CommonResponse<ConcertDate>> response = restTemplate.exchange(url, HttpMethod.GET, header, new ParameterizedTypeReference<>() {});
        CommonResponse<ConcertDate> body = response.getBody();
        ConcertDate concertDate = body.getData();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.TOKEN_EXPIRED.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.TOKEN_EXPIRED.getMessage());
        assertThat(concertDate).isNull();
    }

    @DisplayName("예약 가능한 좌석 목록을 조회한다")
    @Test
    void getAvailableSeats() {
        // given
        setUpConcert();
        Long concertDetailId = findFirstConcertDetailId();
        Long memberId = findFirstMemberId();
        String url = "http://localhost:" + port + "/api/concert/seat/" + concertDetailId;

        // HttpEntity 에 헤더 포함
        HttpEntity<String> header = setHeader(memberId);

        // when
        ResponseEntity<CommonResponse<ConcertSeat>> response = restTemplate.exchange(url, HttpMethod.GET, header, new ParameterizedTypeReference<>() {});
        CommonResponse<ConcertSeat> body = response.getBody();
        ConcertSeat concertDate = body.getData();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.SUCCESS.getMessage());
        assertThat(concertDate.concertDetailId()).isEqualTo(concertDetailId);
        assertThat(concertDate.concertSeats().size()).isEqualTo(5);
        assertThat(concertDate.concertSeats().get(0)).isInstanceOf(ConcertSeatDetail.class);
        assertThat(concertDate.concertSeats().get(0).seatNo()).isEqualTo(1);
    }

    @DisplayName("예약 가능한 좌석 목록을 조회 시 토큰이 없어 실패한다")
    @Test
    void getAvailableSeats_fail_no_token() {
        // given
        setUpConcert();
        Long concertDetailId = findFirstConcertDetailId();
        Long memberId = findFirstMemberId() + 1;
        String url = "http://localhost:" + port + "/api/concert/seat/" + concertDetailId;

        // HttpEntity 에 헤더 포함
        HttpEntity<String> header = setHeader(memberId);

        // when
        ResponseEntity<CommonResponse<ConcertSeat>> response = restTemplate.exchange(url, HttpMethod.GET, header, new ParameterizedTypeReference<>() {});
        CommonResponse<ConcertSeat> body = response.getBody();
        ConcertSeat concertDate = body.getData();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.TOKEN_EXPIRED.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.TOKEN_EXPIRED.getMessage());
        assertThat(concertDate).isNull();
    }
}
