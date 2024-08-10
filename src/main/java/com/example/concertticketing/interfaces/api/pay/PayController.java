package com.example.concertticketing.interfaces.api.pay;

import com.example.concertticketing.interfaces.api.pay.dto.PayRequest;
import com.example.concertticketing.interfaces.api.pay.dto.PayResponse;
import com.example.concertticketing.domain.pay.service.PayService;
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
    public ResponseEntity<PayResponse> pay(@RequestBody PayRequest request) {
        return ResponseEntity.ok(PayResponse.of(payService.pay(request)));
    }
}
