package com.example.concertticketing.interfaces.api.member.dto;

import com.example.concertticketing.domain.member.model.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberBalanceChargeResponse {
    Long balance;

    @Builder
    public MemberBalanceChargeResponse(Long balance) {
        this.balance = balance;
    }

    public static MemberBalanceChargeResponse of(Member member) {
        return MemberBalanceChargeResponse.builder()
                .balance(member.getBalance())
                .build();
    }
}
