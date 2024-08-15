package com.example.concertticketing.domain.pay.repository;

import com.example.concertticketing.domain.pay.infrastructure.PayJpaRepository;
import com.example.concertticketing.domain.pay.model.Pay;
import com.example.concertticketing.exception.CustomException;
import com.example.concertticketing.exception.ErrorEnum;
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

    @Override
    public void deleteAllInBatch() {
        payJpaRepository.deleteAllInBatch();
    }

    @Override
    public Pay findById(Long id) {
        return payJpaRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorEnum.NO_PAY));
    }
}
