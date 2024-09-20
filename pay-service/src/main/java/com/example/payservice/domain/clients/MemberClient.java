package com.example.payservice.domain.clients;

import com.example.payservice.external.MemberChargeRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "MEMBER-SERVICE", url = "${member-service.url}")
public interface MemberClient {

    @PostMapping("/balance")
    void chargeBalance(@RequestBody MemberChargeRequest reservePayRequest);
}
