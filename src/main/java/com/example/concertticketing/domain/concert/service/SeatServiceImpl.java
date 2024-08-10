package com.example.concertticketing.domain.concert.service;

import com.example.concertticketing.domain.concert.model.Seat;
import com.example.concertticketing.domain.concert.repository.SeatRepository;
import com.example.concertticketing.exception.CustomException;
import com.example.concertticketing.exception.ErrorEnum;
import com.example.concertticketing.domain.member.model.Member;
import com.example.concertticketing.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SeatServiceImpl implements SeatService {
    private final SeatRepository seatRepository;
    private final MemberRepository memberRepository;

    @Override
    public Seat selectSeat(Long seatId) {
        return seatRepository.findById(seatId)
                .orElseThrow(() -> new CustomException(ErrorEnum.NO_SEAT));
    }

    @Transactional
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

    @Transactional
    @Override
    public void reserveSeat(Long seatId, LocalDateTime now, Long memberId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new CustomException(ErrorEnum.NO_SEAT));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorEnum.MEMBER_NOT_FOUND));
        seat.updateReservedAt(now);
        seat.updateMember(member);
    }
}
