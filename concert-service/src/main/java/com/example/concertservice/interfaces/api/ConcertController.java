package com.example.concertservice.interfaces.api;

import com.example.concertservice.domain.model.ConcertDate;
import com.example.concertservice.domain.service.ConcertService;
import com.example.concertservice.domain.service.SeatService;
import com.example.concertservice.interfaces.api.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/concert")
public class ConcertController {

    private final ConcertService concertService;
    private final SeatService seatService;

    /**
     * 예약 가능한 날짜 목록 조회
     * */
    @GetMapping("/date/{concertId}")
    public ResponseEntity<CommonResponse> getAvailableDates(@PathVariable("concertId") Long concertId) {
        ConcertDate concertDate = concertService.selectAvailableDates(concertId);
        return ResponseEntity.ok(CommonResponse.success(concertDate));
    }

    /**
     * 예약 가능 좌석 조회
     * */
    @GetMapping("/seat/{concertDetailId}")
    public ResponseEntity<CommonResponse> getAvailableSeats(
              @PathVariable("concertDetailId") Long concertDetailId) {
        return ResponseEntity.ok(CommonResponse.success(concertService.selectAvailableSeats(concertDetailId)));
    }

    @GetMapping("/{seatId}")
    public ResponseEntity<CommonResponse> getSeatById(
            @PathVariable("seatId") Long seatId) {
        return ResponseEntity.ok(CommonResponse.success(concertService.getSeatById(seatId)));
    }

    @GetMapping("/{seatId}/{memberId}")
    public void reserveSeat(
            @PathVariable("seatId") Long seatId,
            @PathVariable("memberId") Long memberId
    ) {
        seatService.reserveSeat(seatId, LocalDateTime.now(), memberId);
    }

    @PostMapping("/seat")
    public void confirmSeat(@RequestBody Long seatId) {
        seatService.confirmSeat(seatId);
    }
}

