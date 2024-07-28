package com.example.concertticketing.domain.member.infrastructure;

import com.example.concertticketing.domain.member.model.Member;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member,Long> {
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT m FROM Member m WHERE m.id = :memberId")
    Optional<Member> selectMemberWithLock(@Param("memberId") Long memberId);
}
