package com.example.concertticketing.api.member.dto;

public record MemberChargeRequest(
        Long memberId,
        Long balance
) {

}
