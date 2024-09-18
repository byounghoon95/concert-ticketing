package com.example.queueservice.interfaces.api;

import com.example.queueservice.domain.service.QueueService;
import com.example.queueservice.interfaces.api.dto.QueueRequest;
import com.example.queueservice.interfaces.api.dto.QueueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/queue")
public class QueueController {

    private final QueueService queueService;

    /**
     * 토큰 발급
     * */
    @PostMapping("/issue")
    public ResponseEntity<QueueResponse> enqueue(@RequestBody QueueRequest request) {
        return ResponseEntity.ok(QueueResponse.of(queueService.enqueue(request.memberId())));
    }

    /**
     * 토큰 정보는 대기열 순서 반환
     * */
    @GetMapping("/{memberId}")
    public ResponseEntity<QueueResponse> getInfo(@PathVariable Long memberId) {
        return ResponseEntity.ok(QueueResponse.of(queueService.getInfo(memberId)));
    }
}
