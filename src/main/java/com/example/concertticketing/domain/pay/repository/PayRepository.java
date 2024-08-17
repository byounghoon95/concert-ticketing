package com.example.concertticketing.domain.pay.repository;


import com.example.concertticketing.domain.pay.model.Pay;

public interface PayRepository {

    Pay pay(Pay pay);

    void deleteAllInBatch();
    Pay findById(Long id);
}
