package com.example.memberservice.domain.service;

import com.example.memberservice.domain.model.Member;
import com.example.memberservice.domain.repository.MemberRepository;
import com.example.memberservice.exception.CustomException;
import com.example.memberservice.exception.ErrorEnum;
import com.example.memberservice.interfaces.api.dto.MemberChargeRequest;
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
}
