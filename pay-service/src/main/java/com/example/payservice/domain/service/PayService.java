package com.example.payservice.domain.service;

import com.example.payservice.domain.model.Pay;
import com.example.payservice.interfaces.api.dto.PayRequest;

public interface PayService {
    Pay pay(PayRequest request);
    void republish();
}
