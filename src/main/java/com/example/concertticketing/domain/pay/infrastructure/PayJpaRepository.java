package com.example.concertticketing.domain.pay.infrastructure;

import com.example.concertticketing.domain.pay.model.Pay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayJpaRepository extends JpaRepository<Pay,Long> {
}
