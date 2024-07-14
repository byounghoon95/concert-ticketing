package com.example.concertticketing.domain.member.service;

import com.example.concertticketing.api.member.dto.MemberChargeRequest;
import com.example.concertticketing.domain.member.model.Member;

public interface MemberService {
    Member getReferenceById(Long memberId);
    Member getBalance(Long memberId);
    Member chargeBalance(MemberChargeRequest request);
}
