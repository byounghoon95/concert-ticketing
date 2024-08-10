package com.example.concertticketing.interfaces.api.reservation;

import com.example.concertticketing.CommonControllerIntegrateTest;
import com.example.concertticketing.interfaces.api.common.response.CommonResponse;
import com.example.concertticketing.interfaces.api.reservation.dto.ReserveSeatRequest;
import com.example.concertticketing.interfaces.api.reservation.dto.ReserveSeatResponse;
import com.example.concertticketing.domain.concert.model.Seat;
import com.example.concertticketing.domain.exception.ErrorEnum;
import com.example.concertticketing.domain.reservation.model.ReservationStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationControllerIntegrateTest extends CommonControllerIntegrateTest {

    @AfterEach
    void tearDown() {
        queueRepository.flushAll();
        memberRepository.deleteAllInBatch();
        concertRepository.deleteAllInBatch();
        seatRepository.deleteAllInBatch();
    }

    @DisplayName("좌석을 예약한다")
    @Test
    void reserveSeat() {
        setUpReservation();
        Long memberId = findFirstMemberId();
        Seat seat = findFirstSeat();
        Long seatId = seat.getId();
        String url = "http://localhost:" + port + "/api/reserve";

        ReserveSeatRequest request = new ReserveSeatRequest(seatId,memberId);

        // when
        HttpEntity<ReserveSeatRequest> header = setHeader(memberId, request);
        ResponseEntity<CommonResponse<ReserveSeatResponse>> response = restTemplate.exchange(url, HttpMethod.POST, header, new ParameterizedTypeReference<>() {});
        CommonResponse<ReserveSeatResponse> body = response.getBody();
        ReserveSeatResponse data = body.getData();

        Seat updatedSeat = seatRepository.findById(seatId).get();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.SUCCESS.getMessage());
        assertThat(data.getSeatNo()).isEqualTo(seat.getSeatNo());
        assertThat(data.getStatus()).isEqualTo(ReservationStatus.RESERVED);

        assertThat(updatedSeat.getMember().getId()).isEqualTo(memberId);
        assertThat(seat.getReservedAt()).isNull();
        assertThat(updatedSeat.getReservedAt()).isNotNull();
    }

    @DisplayName("임시저장된 좌석 예약 시 에러를 반환한다")
    @Test
    void reserveSeat_already_reserved() {
        setUpReservation();
        Long memberId = findFirstMemberId();
        Seat seat = findFirstSeat();
        Long seatId = seat.getId() + 1;
        String url = "http://localhost:" + port + "/api/reserve";

        ReserveSeatRequest request = new ReserveSeatRequest(seatId,memberId);

        // when
        HttpEntity<ReserveSeatRequest> header = setHeader(memberId, request);
        ResponseEntity<CommonResponse<ReserveSeatResponse>> response = restTemplate.exchange(url, HttpMethod.POST, header, new ParameterizedTypeReference<>() {});
        CommonResponse<ReserveSeatResponse> body = response.getBody();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.RESERVED_SEAT.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.RESERVED_SEAT.getMessage());
    }

}
