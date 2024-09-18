package com.example.payservice.domain.repository;


import com.example.payservice.domain.model.Pay;

public interface PayRepository {

    Pay pay(Pay pay);

    void deleteAllInBatch();
    Pay findById(Long id);
}
