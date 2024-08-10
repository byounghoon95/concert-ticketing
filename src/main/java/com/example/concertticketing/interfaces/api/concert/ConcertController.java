package com.example.concertticketing.interfaces.api.concert;

import com.example.concertticketing.domain.concert.model.ConcertDate;
import com.example.concertticketing.domain.concert.model.ConcertSeat;
import com.example.concertticketing.domain.concert.service.ConcertService;
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
    public ResponseEntity<ConcertDate> getAvailableDates(@PathVariable("concertId") Long concertId) {
        ConcertDate concertDate = concertService.selectAvailableDates(concertId);
        return ResponseEntity.ok(concertDate);
    }

    /**
     * 예약 가능 좌석 조회
     * */
    @GetMapping("/seat/{concertDetailId}")
    public ResponseEntity<ConcertSeat> getAvailableSeats(
              @PathVariable("concertDetailId") Long concertDetailId) {
        return ResponseEntity.ok(concertService.selectAvailableSeats(concertDetailId));
    }
}

