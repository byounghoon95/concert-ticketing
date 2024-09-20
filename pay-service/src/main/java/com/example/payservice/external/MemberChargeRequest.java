package com.example.payservice.external;

public record MemberChargeRequest(
        Long memberId,
        Long balance
) {
}
