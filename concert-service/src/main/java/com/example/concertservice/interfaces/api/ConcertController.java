package com.example.concertservice.interfaces.api;

import com.example.concertservice.domain.model.ConcertDate;
import com.example.concertservice.domain.model.ConcertSeat;
import com.example.concertservice.domain.service.ConcertService;
import com.example.concertservice.interfaces.api.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/concert")
public class ConcertController {

    private final ConcertService concertService;

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
}

