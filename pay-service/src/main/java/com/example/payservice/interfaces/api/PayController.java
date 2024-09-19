package com.example.payservice.interfaces.api;

import com.example.payservice.domain.service.PayService;
import com.example.payservice.interfaces.api.common.response.CommonResponse;
import com.example.payservice.interfaces.api.dto.PayRequest;
import com.example.payservice.interfaces.api.dto.PayResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pay")
public class PayController {
    private final PayService payService;

    /**
     * 결제 요청
     * */
    @PostMapping("")
    public ResponseEntity<CommonResponse> pay(@RequestBody PayRequest request) {
        return ResponseEntity.ok(CommonResponse.success(PayResponse.of(payService.pay(request))));
    }
}
