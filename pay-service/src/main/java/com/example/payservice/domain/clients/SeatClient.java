package com.example.payservice.domain.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "CONCERT-SERVICE", url = "${concert-service.url}")
public interface SeatClient {
    @PostMapping("/seat")
    void confirmSeat(@RequestBody Long seatId);
}
