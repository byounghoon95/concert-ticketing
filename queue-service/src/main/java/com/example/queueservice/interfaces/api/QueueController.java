package com.example.queueservice.interfaces.api;

import com.example.queueservice.domain.service.QueueService;
import com.example.queueservice.interfaces.api.common.response.CommonResponse;
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
    public ResponseEntity<CommonResponse> enqueue(@RequestBody QueueRequest request) {
        return ResponseEntity.ok(CommonResponse.success(QueueResponse.of(queueService.enqueue(request.memberId()))));
    }

    /**
     * 토큰 정보는 대기열 순서 반환
     * */
    @GetMapping("/{memberId}")
    public ResponseEntity<CommonResponse> getInfo(@PathVariable Long memberId) {
        return ResponseEntity.ok(CommonResponse.success(QueueResponse.of(queueService.getInfo(memberId))));
    }

    @PostMapping("/expire")
    public void expireActiveToken(@RequestBody Long memberId) {
        queueService.expireActiveToken(memberId);
    }

    @PostMapping("/verify")
    public boolean verifyToken(@RequestBody Long memberId) {
        return queueService.verify(memberId);
    }

}
