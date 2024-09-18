package com.example.payservice.infrastructure;


import com.example.payservice.domain.model.Pay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayJpaRepository extends JpaRepository<Pay,Long> {
}
