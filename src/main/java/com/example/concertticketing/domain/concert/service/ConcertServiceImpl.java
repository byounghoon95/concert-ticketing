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
        List<ConcertDateDetails> details = concertRepository.findDatesByConcertId(concertId).stream()
                .map(entity -> new ConcertDateDetails(entity.getId(), entity.getDate()))
                .collect(Collectors.toList());
        return new ConcertDate(concertId, details);
    }

    @Override
    public ConcertSeat selectAvailableSeats(Long concertDetailId) {
        List<ConcertSeatDetail> details = concertRepository.findAvailableSeatsByConcertId(concertDetailId, LocalDateTime.now().minusSeconds(10)).stream()
                .map(entity -> new ConcertSeatDetail(entity.getId(), entity.getSeatNo()))
                .collect(Collectors.toList());
        return new ConcertSeat(concertDetailId, details);
    }
}
