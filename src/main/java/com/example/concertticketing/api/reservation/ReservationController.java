package com.example.concertticketing.api.reservation;

import com.example.concertticketing.api.reservation.dto.ReserveSeatRequest;
import com.example.concertticketing.api.reservation.dto.ReserveSeatResponse;
import com.example.concertticketing.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reserve")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 좌석 예약
     * */
    @PostMapping("")
    public ResponseEntity<ReserveSeatResponse> reserveSeat(@RequestBody ReserveSeatRequest request) {
        return ResponseEntity.ok(ReserveSeatResponse.of(reservationService.reserveSeat(request.seatId(), request.memberId())));
    }
}
