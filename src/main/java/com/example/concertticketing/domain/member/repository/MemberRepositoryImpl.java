package com.example.concertticketing.domain.member.repository;

import com.example.concertticketing.domain.member.infrastructure.MemberJpaRepository;
import com.example.concertticketing.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Optional<Member> findById(Long memberId) {
        return memberJpaRepository.findById(memberId);
    }

    @Override
    public Member getReferenceById(Long id) {
        return memberJpaRepository.getReferenceById(id);
    }

    @Override
    public void save(Member member) {
        memberJpaRepository.save(member);
    }

    @Override
    public void deleteAllInBatch() {
        memberJpaRepository.deleteAllInBatch();
    }
}
