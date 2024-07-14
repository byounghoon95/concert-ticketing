package com.example.concertticketing.domain.pay.repository;

import com.example.concertticketing.domain.pay.infrastructure.PayJpaRepository;
import com.example.concertticketing.domain.pay.model.Pay;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PayRepositoryImpl implements PayRepository {

    private final PayJpaRepository payJpaRepository;

    @Override
    public Pay pay(Pay pay) {
        return payJpaRepository.save(pay);
    }
}
