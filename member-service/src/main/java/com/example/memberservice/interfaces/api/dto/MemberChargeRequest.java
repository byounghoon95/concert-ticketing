package com.example.memberservice.interfaces.api.dto;

public record MemberChargeRequest(
        Long memberId,
        Long balance
) {

}
