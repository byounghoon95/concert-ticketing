package com.example.concertservice.domain.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "QUEUE-SERVICE", url = "${queue-service.url}")
public interface QueueClient {
    @PostMapping("/verify")
    boolean verifyToken(@RequestBody Long memberId);
}
