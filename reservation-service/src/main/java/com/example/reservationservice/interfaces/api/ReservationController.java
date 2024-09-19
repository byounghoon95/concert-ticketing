package com.example.reservationservice.interfaces.api;

import com.example.reservationservice.application.ReservationFacade;
import com.example.reservationservice.interfaces.api.dto.ReserveSeatRequest;
import com.example.reservationservice.interfaces.api.dto.ReserveSeatResponse;
import com.example.reservationservice.interfaces.common.response.CommonResponse;
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

    private final ReservationFacade reservationFacade;

    /**
     * 좌석 예약
     * */
    @PostMapping("")
    public ResponseEntity<CommonResponse> reserveSeat(@RequestBody ReserveSeatRequest request) {
        return ResponseEntity.ok(CommonResponse.success(ReserveSeatResponse.of(reservationFacade.reserveSeat(request.seatId(), request.memberId()))));
    }
}
