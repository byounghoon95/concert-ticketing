package com.example.reservationservice.domain.clients;

import com.example.reservationservice.domain.external.SeatResponse;
import com.example.reservationservice.interfaces.api.common.response.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SEAT-SERVICE", url = "${concert-service.url}")
public interface SeatClient {

    @GetMapping("/{seatId}")
    CommonResponse<SeatResponse> findById(@PathVariable("seatId") Long seatId);

    @GetMapping("/{seatId}/{memberId}")
    void reserveSeat(@PathVariable("seatId") Long seatId, @PathVariable("memberId") Long memberId);
}
