package com.example.concertticketing.domain.member.service;

import com.example.concertticketing.api.member.dto.MemberChargeRequest;
import com.example.concertticketing.domain.exception.CustomException;
import com.example.concertticketing.domain.exception.ErrorEnum;
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
                .orElseThrow(() -> new CustomException(ErrorEnum.MEMBER_NOT_FOUND));
    }

    @Transactional
    @Override
    public Member chargeBalance(MemberChargeRequest request) {
        Member member = memberRepository.selectMemberWithLock(request.memberId())
                .orElseThrow(() -> new CustomException(ErrorEnum.MEMBER_NOT_FOUND));

        member.chargeBalance(request.balance());

        return member;
    }

    @Override
    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorEnum.MEMBER_NOT_FOUND));
    }

    @Override
    public void minusBalance(Long memberId, Long price) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorEnum.MEMBER_NOT_FOUND));

        member.minusBalance(price);
    }
}
