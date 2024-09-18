package com.example.memberservice.domain.repository;


import com.example.memberservice.domain.model.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Optional<Member> findById(Long memberId);
    Member getReferenceById(Long id);
    Member save(Member member);
    void saveAll(List<Member> member);
    void deleteAllInBatch();
    List<Member> findAll();
    Optional<Member> selectMemberWithLock(Long memberId);
}
