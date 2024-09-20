package com.example.payservice.domain.clients;

import com.example.payservice.external.ReservePayResponse;
import com.example.payservice.interfaces.api.common.response.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "RESERVATION-SERVICE", url = "${reservation-service.url}")
public interface ReservationClient {

    @GetMapping("/verify")
    CommonResponse<ReservePayResponse> verifyReservation(@RequestParam(name = "reservationId") Long reservationId, @RequestParam(name = "memberId") Long memberId);
}
