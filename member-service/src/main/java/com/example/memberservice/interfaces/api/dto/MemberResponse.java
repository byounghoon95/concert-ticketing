package com.example.memberservice.interfaces.api.dto;

import com.example.memberservice.domain.model.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberResponse {
    Long balance;

    @Builder
    public MemberResponse(Long balance) {
        this.balance = balance;
    }

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                .balance(member.getBalance())
                .build();
    }
}
