package com.example.concertservice.domain.repository;

import com.example.concertservice.domain.model.Concert;
import com.example.concertservice.domain.model.ConcertDate;
import com.example.concertservice.domain.model.ConcertDetail;
import com.example.concertservice.domain.model.Seat;
import com.example.concertservice.exception.CustomException;
import com.example.concertservice.exception.ErrorEnum;
import com.example.concertservice.infrastructure.ConcertDetailJpaRepository;
import com.example.concertservice.infrastructure.ConcertJpaRepository;
import com.example.concertservice.infrastructure.SeatJpaRepository;
import com.example.concertservice.util.JsonConverter;
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
        return seatJpaRepository.findAvailableSeats(concertDetailId, time);
    }

    @Override
    public Concert saveConcert(Concert concert) {
        return concertJpaRepository.save(concert);
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
    public ConcertDetail findConcertDetail(Long concertDetailId) {
        return concertDetailJpaRepository.findById(concertDetailId)
                .orElseThrow(() -> new CustomException(ErrorEnum.NO_CONCERT));
    }

    @Override
    public void deleteAllInBatch() {
        concertDetailJpaRepository.deleteAllInBatch();
        concertJpaRepository.deleteAllInBatch();
    }
}
