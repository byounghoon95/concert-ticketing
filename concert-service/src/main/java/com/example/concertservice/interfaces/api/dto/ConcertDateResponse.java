package com.example.concertservice.interfaces.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ConcertDateResponse {
    List<String> dates;

    @Builder
    public ConcertDateResponse(List<String> dates) {
        this.dates = dates;
    }
}
