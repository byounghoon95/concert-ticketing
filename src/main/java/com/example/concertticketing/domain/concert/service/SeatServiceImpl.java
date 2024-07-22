package com.example.concertticketing.domain.concert.service;

import com.example.concertticketing.domain.concert.model.Seat;
import com.example.concertticketing.domain.concert.repository.SeatRepository;
import com.example.concertticketing.domain.exception.CustomException;
import com.example.concertticketing.domain.exception.ErrorEnum;
import com.example.concertticketing.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SeatServiceImpl implements SeatService {
    private final SeatRepository seatRepository;

    @Override
    public Seat selectSeat(Long seatId) {
        return seatRepository.findById(seatId)
                .orElseThrow(() -> new CustomException(ErrorEnum.NO_SEAT));
    }

    @Override
    public Seat selectSeatWithLock(Long seatId) {
        return seatRepository.selectSeatWithLock(seatId)
                .orElseThrow(() -> new CustomException(ErrorEnum.NO_SEAT));
    }

    @Transactional
    @Override
    public void updateReservedAt(Long seatId, LocalDateTime now) {
        Seat seat = selectSeat(seatId);
        seat.updateReservedAt(now);
    }

    @Override
    public void reserveSeat(Seat seat, LocalDateTime now, Member member) {
        seat.updateReservedAt(now);
        seat.updateMember(member);
    }
}
