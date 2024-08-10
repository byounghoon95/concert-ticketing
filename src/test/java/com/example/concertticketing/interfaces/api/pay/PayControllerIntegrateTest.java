package com.example.concertticketing.interfaces.api.pay;

import com.example.concertticketing.CommonControllerIntegrateTest;
import com.example.concertticketing.interfaces.api.common.response.CommonResponse;
import com.example.concertticketing.interfaces.api.pay.dto.PayRequest;
import com.example.concertticketing.interfaces.api.pay.dto.PayResponse;
import com.example.concertticketing.domain.concert.model.Seat;
import com.example.concertticketing.exception.ErrorEnum;
import com.example.concertticketing.domain.member.model.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class PayControllerIntegrateTest extends CommonControllerIntegrateTest {

    @AfterEach
    void tearDown() {
        queueRepository.flushAll();
        memberRepository.deleteAllInBatch();
        concertRepository.deleteAllInBatch();
        reservationRepository.deleteAllInBatch();
        seatRepository.deleteAllInBatch();
    }

    @DisplayName("예약건을 결제한다")
    @Test
    void pay() {
        // given
        setUpPay();
        Long memberId = findFirstMemberId();
        Long queueId = findFirstMemberId();
        Seat seat = findFirstSeat();
        Long seatId = seat.getId() + 1L;
        Long reservationId = findFirstReservationId();
        String url = "http://localhost:" + port + "/api/pay";

        PayRequest request = new PayRequest(reservationId,seatId,memberId);

        // when
        HttpEntity<PayRequest> header = setHeader(memberId, request);
        ResponseEntity<CommonResponse<PayResponse>> response = restTemplate.exchange(url, HttpMethod.POST, header, new ParameterizedTypeReference<>() {});
        CommonResponse<PayResponse> body = response.getBody();
        PayResponse data = body.getData();

        Member savedMember = memberRepository.findById(memberId).get();
        Seat savedSeat = seatRepository.findById(seatId).get();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.SUCCESS.getMessage());
        assertThat(data.getSeatNo()).isEqualTo(savedSeat.getSeatNo());
        assertThat(data.getAmount()).isEqualTo(4000L);
        assertThat(savedSeat.getReservedAt()).isEqualTo(LocalDateTime.of(9999, 12, 31, 23, 59, 59));
        assertThat(savedMember.getBalance()).isEqualTo(1000L);
    }

    @DisplayName("예약건 결제에서 요청의 멤버 아이디와 예약의 멤버 아이다가 달라 에러를 반환한다")
    @Test
    void pay_member_not_match() {
        // given
        setUpPay();
        Long memberId = findFirstMemberId();
        Seat seat = findFirstSeat();
        Long seatId = seat.getId() + 1L;
        Long reservationId = findFirstReservationId();
        String url = "http://localhost:" + port + "/api/pay";

        PayRequest request = new PayRequest(reservationId,seatId,memberId + 1);

        // when
        HttpEntity<PayRequest> header = setHeader(memberId, request);
        ResponseEntity<CommonResponse<PayResponse>> response = restTemplate.exchange(url, HttpMethod.POST, header, new ParameterizedTypeReference<>() {});
        CommonResponse<PayResponse> body = response.getBody();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.MEMBER_NOT_MATCH.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.MEMBER_NOT_MATCH.getMessage());
    }

    @DisplayName("잔액이 부족해 에러를 반환한다")
    @Test
    void pay_member_not_enough_balance() {
        // given
        setUpPay();
        Long memberId = findFirstMemberId();
        Seat seat = findFirstSeat();
        Long seatId = seat.getId() + 2L;
        Long reservationId = findFirstReservationId() + 1L;
        String url = "http://localhost:" + port + "/api/pay";

        PayRequest request = new PayRequest(reservationId,seatId,memberId);

        // when
        HttpEntity<PayRequest> header = setHeader(memberId, request);
        ResponseEntity<CommonResponse<PayResponse>> response = restTemplate.exchange(url, HttpMethod.POST, header, new ParameterizedTypeReference<>() {});
        CommonResponse<PayResponse> body = response.getBody();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.NOT_ENOUGH_BALANCE.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.NOT_ENOUGH_BALANCE.getMessage());
    }
}
