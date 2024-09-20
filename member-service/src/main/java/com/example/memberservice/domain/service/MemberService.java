package com.example.memberservice.domain.service;

import com.example.memberservice.domain.model.Member;
import com.example.memberservice.interfaces.api.dto.MemberChargeRequest;

public interface MemberService {
    Member getReferenceById(Long memberId);
    Member getBalance(Long memberId);
    Member chargeBalance(MemberChargeRequest request);
    Member findById(Long memberId);
}
