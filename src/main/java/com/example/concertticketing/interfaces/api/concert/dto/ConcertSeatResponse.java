package com.example.concertticketing.interfaces.api.concert.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ConcertSeatResponse {
    List<Integer> seats;

    @Builder
    public ConcertSeatResponse(List<Integer> seats) {
        this.seats = seats;
    }
}
