package com.example.concertticketing.domain.concert.service;

import com.example.concertticketing.domain.concert.model.ConcertDate;
import com.example.concertticketing.domain.concert.model.ConcertDateDetails;
import com.example.concertticketing.domain.concert.model.ConcertSeat;
import com.example.concertticketing.domain.concert.model.ConcertSeatDetail;
import com.example.concertticketing.domain.concert.repository.ConcertRepository;
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
}