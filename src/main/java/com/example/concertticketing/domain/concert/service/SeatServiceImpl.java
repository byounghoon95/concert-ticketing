package com.example.concertticketing.domain.concert.service;

import com.example.concertticketing.domain.concert.model.Seat;
import com.example.concertticketing.domain.concert.repository.SeatRepository;
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
                .orElseThrow(() -> new NullPointerException("좌석 정보가 존재하지 않습니다"));
    }

    @Transactional
    @Override
    public void updateReservedAt(Long seatId, LocalDateTime now) {
        Seat seat = selectSeat(seatId);
        seat.updateReservedAt(now);
    }

    @Transactional
    @Override
    public void updateMember(Long seatId, Member member) {
        Seat seat = selectSeat(seatId);
        seat.updateMember(member);
    }
}
