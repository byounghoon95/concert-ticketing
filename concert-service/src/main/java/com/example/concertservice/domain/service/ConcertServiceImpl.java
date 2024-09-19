package com.example.concertservice.domain.service;

import com.example.concertservice.domain.model.*;
import com.example.concertservice.domain.repository.ConcertRepository;
import com.example.concertservice.domain.repository.SeatRepository;
import com.example.concertservice.exception.CustomException;
import com.example.concertservice.exception.ErrorEnum;
import com.example.concertservice.interfaces.api.dto.SeatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ConcertServiceImpl implements ConcertService {

    private final ConcertRepository concertRepository;
    private final SeatRepository seatRepository;

    @Override
    public ConcertDate selectAvailableDates(Long concertId) {
        ConcertDate cacheData = concertRepository.findConcertDatesFromCache(concertId);
        if (cacheData != null) {
            return cacheData;
        }

        List<ConcertDateDetails> details = concertRepository.findConcertDates(concertId).stream()
                .map(entity -> new ConcertDateDetails(entity.getId(), entity.getDate()))
                .collect(Collectors.toList());

        ConcertDate concertDate = new ConcertDate(concertId, details);
        concertRepository.addCache(concertId,concertDate);

        return concertDate;
    }

    @Override
    public ConcertSeat selectAvailableSeats(Long concertDetailId) {
        List<ConcertSeatDetail> details = concertRepository.findAvailableSeats(concertDetailId, LocalDateTime.now().minusMinutes(5)).stream()
                .map(entity -> new ConcertSeatDetail(entity.getId(), entity.getSeatNo()))
                .collect(Collectors.toList());
        return new ConcertSeat(concertDetailId, details);
    }

    @Override
    public SeatResponse getSeatById(Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new CustomException(ErrorEnum.NO_SEAT));
        return SeatResponse.of(seat,seat.getConcert().getId());
    }
}