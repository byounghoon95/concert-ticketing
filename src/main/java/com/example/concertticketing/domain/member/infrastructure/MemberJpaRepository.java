package com.example.concertticketing.domain.member.infrastructure;

import com.example.concertticketing.domain.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<Member,Long> {
}
