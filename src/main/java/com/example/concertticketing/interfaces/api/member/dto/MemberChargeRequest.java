package com.example.concertticketing.interfaces.api.member.dto;

public record MemberChargeRequest(
        Long memberId,
        Long balance
) {

}
