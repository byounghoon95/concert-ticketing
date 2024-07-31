package com.example.concertticketing.domain.concert.repository;

import com.example.concertticketing.domain.concert.infrastructure.ConcertDetailJpaRepository;
import com.example.concertticketing.domain.concert.infrastructure.ConcertJpaRepository;
import com.example.concertticketing.domain.concert.infrastructure.SeatJpaRepository;
import com.example.concertticketing.domain.concert.model.Concert;
import com.example.concertticketing.domain.concert.model.ConcertDate;
import com.example.concertticketing.domain.concert.model.ConcertDetail;
import com.example.concertticketing.domain.concert.model.Seat;
import com.example.concertticketing.domain.util.JsonConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class ConcertRepositoryImpl implements ConcertRepository {

    private final ConcertJpaRepository concertJpaRepository;
    private final ConcertDetailJpaRepository concertDetailJpaRepository;
    private final SeatJpaRepository seatJpaRepository;
    private final RedisTemplate redisTemplate;
    private final JsonConverter jsonConverter;
    private static final String CACHE_KEY = "concertDate";

    @Override
    public List<ConcertDetail> findConcertDates(Long concertId) {
        return concertDetailJpaRepository.findByConcertId(concertId);
    }

    @Override
    public List<Seat> findAvailableSeats(Long concertDetailId, LocalDateTime time) {
        return seatJpaRepository.findByConcertIdAndReservedAtIsNullOrReservedAtBefore(concertDetailId, time);
    }

    @Override
    public void saveConcert(Concert concert) {
        concertJpaRepository.save(concert);
    }

    @Override
    public void saveConcertDetailAll(List<ConcertDetail> detailList) {
        concertDetailJpaRepository.saveAll(detailList);
    }

    @Override
    public ConcertDate findConcertDatesFromCache(Long concertId) {
        Object value = redisTemplate.opsForValue().get(CACHE_KEY + ":" + concertId);
        if (value == null) {
            return null;
        }

        return jsonConverter.fromJson(value.toString(), ConcertDate.class);
    }

    @Override
    public void addCache(Long concertId, ConcertDate concertDate) {
        redisTemplate.opsForValue().set(CACHE_KEY + ":" + concertId, jsonConverter.toJson(concertDate), 5, TimeUnit.MINUTES);
    }

    @Override
    public void deleteAllInBatch() {
        concertDetailJpaRepository.deleteAllInBatch();
        concertJpaRepository.deleteAllInBatch();
    }
}
