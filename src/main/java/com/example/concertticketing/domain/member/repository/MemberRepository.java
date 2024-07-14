package com.example.concertticketing.domain.member.repository;


import com.example.concertticketing.domain.member.model.Member;

import java.util.Optional;

public interface MemberRepository {
    Optional<Member> findById(Long memberId);
    Member getReferenceById(Long id);
}
