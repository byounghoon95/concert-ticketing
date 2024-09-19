package com.example.concertservice.domain.service;

import com.example.concertservice.domain.model.Seat;
import com.example.concertservice.domain.model.SeatCompensation;
import com.example.concertservice.domain.repository.SeatRepository;
import com.example.concertservice.exception.CustomException;
import com.example.concertservice.exception.ErrorEnum;
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

    @Transactional
    @Override
    public Seat selectSeatWithLock(Long seatId) {
        return seatRepository.selectSeatWithLock(seatId)
                .orElseThrow(() -> new CustomException(ErrorEnum.NO_SEAT));
    }

    @Override
    public void rollbackSeat(SeatCompensation seatComp) {
        Seat seat = seatRepository.findById(seatComp.seatId())
                .orElseThrow(() -> new CustomException(ErrorEnum.NO_SEAT));

        seat.updateReservedAt(seatComp.reservedAt());
        seat.updateMemberId(seatComp.memberId());
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

        seat.updateReservedAt(now);
        seat.updateMemberId(memberId);
    }
}
