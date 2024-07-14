package com.example.concertticketing.domain.member.service;

import com.example.concertticketing.api.member.dto.MemberChargeRequest;
import com.example.concertticketing.domain.member.model.Member;
import com.example.concertticketing.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public Member getReferenceById(Long id) {
        return memberRepository.getReferenceById(id);
    }

    @Override
    public Member getBalance(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NullPointerException("멤버가 존재하지 않습니다"));
    }

    @Transactional
    @Override
    public Member chargeBalance(MemberChargeRequest request) {
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new NullPointerException("멤버가 존재하지 않습니다"));

        member.chargeBalance(request.balance());

        return member;
    }
}
