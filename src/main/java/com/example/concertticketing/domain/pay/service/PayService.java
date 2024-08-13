package com.example.concertticketing.domain.pay.service;

import com.example.concertticketing.interfaces.api.pay.dto.PayRequest;
import com.example.concertticketing.domain.pay.model.Pay;

public interface PayService {
    Pay pay(PayRequest request);
    void republish();
}
