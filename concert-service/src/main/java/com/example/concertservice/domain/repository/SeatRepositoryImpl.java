package com.example.concertservice.domain.repository;

import com.example.concertservice.domain.model.Seat;
import com.example.concertservice.infrastructure.SeatJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class SeatRepositoryImpl implements SeatRepository {

    private final SeatJpaRepository seatJpaRepository;

    @Override
    public Optional<Seat> findById(Long seatId) {
        return seatJpaRepository.findById(seatId);
    }

    @Override
    public Optional<Seat> selectSeatWithLock(Long seatId) {
        return seatJpaRepository.selectSeatWithLock(seatId);
    }

    @Override
    public Seat save(Seat seat) {
        return seatJpaRepository.save(seat);
    }

    @Override
    public void saveAll(List<Seat> seats) {
        seatJpaRepository.saveAll(seats);
    }

    @Override
    public void deleteAllInBatch() {
        seatJpaRepository.deleteAllInBatch();
    }
}
