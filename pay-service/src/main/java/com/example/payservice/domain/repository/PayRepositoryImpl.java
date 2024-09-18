package com.example.payservice.domain.repository;

import com.example.payservice.domain.model.Pay;
import com.example.payservice.exception.CustomException;
import com.example.payservice.exception.ErrorEnum;
import com.example.payservice.infrastructure.PayJpaRepository;
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
