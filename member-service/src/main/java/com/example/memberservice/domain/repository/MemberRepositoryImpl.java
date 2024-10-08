package com.example.memberservice.domain.repository;

import com.example.memberservice.domain.model.Member;
import com.example.memberservice.infrastructure.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    public Member save(Member member) {
        return memberJpaRepository.save(member);
    }

    @Override
    public void saveAll(List<Member> member) {
        memberJpaRepository.saveAll(member);
    }

    @Override
    public void deleteAllInBatch() {
        memberJpaRepository.deleteAllInBatch();
    }

    @Override
    public List<Member> findAll() {
        return memberJpaRepository.findAll();
    }

    @Override
    public Optional<Member> selectMemberWithLock(Long memberId) {
        return memberJpaRepository.selectMemberWithLock(memberId);
    }
}
