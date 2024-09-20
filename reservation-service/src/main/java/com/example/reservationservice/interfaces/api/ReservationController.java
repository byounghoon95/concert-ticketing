package com.example.reservationservice.interfaces.api;

import com.example.reservationservice.application.ReservationFacade;
import com.example.reservationservice.domain.service.ReservationService;
import com.example.reservationservice.interfaces.api.dto.ReservePayResponse;
import com.example.reservationservice.interfaces.api.dto.ReserveSeatRequest;
import com.example.reservationservice.interfaces.api.dto.ReserveSeatResponse;
import com.example.reservationservice.interfaces.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reserve")
public class ReservationController {

    private final ReservationFacade reservationFacade;
    private final ReservationService reservationService;

    /**
     * 좌석 예약
     * */
    @PostMapping("")
    public ResponseEntity<CommonResponse> reserveSeat(@RequestBody ReserveSeatRequest request) {
        return ResponseEntity.ok(CommonResponse.success(ReserveSeatResponse.of(reservationFacade.reserveSeat(request.seatId(), request.memberId()))));
    }

    @GetMapping("/verify")
    public ResponseEntity<CommonResponse> verifyReservation(@RequestParam Long reservationId, @RequestParam Long memberId) {
        return ResponseEntity.ok(CommonResponse.success(ReservePayResponse.of(reservationService.verifyReservation(reservationId, memberId))));
    }
}
