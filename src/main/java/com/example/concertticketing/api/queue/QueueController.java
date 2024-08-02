package com.example.concertticketing.api.queue;

import com.example.concertticketing.api.queue.dto.QueueRequest;
import com.example.concertticketing.api.queue.dto.QueueResponse;
import com.example.concertticketing.domain.queue.service.QueueService;
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
