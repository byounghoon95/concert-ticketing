package com.example.concertticketing.domain.member.service;

import com.example.concertticketing.api.member.dto.MemberChargeRequest;
import com.example.concertticketing.domain.member.model.Member;
import com.example.concertticketing.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    @DisplayName("멤버의 현재 잔액을 조회한다")
    @Test
    void getBalance() {
        // given
        Long memberId = 1L;
        Long balance = 5000L;

        Member mockMember = Member.builder()
                .balance(balance)
                .build();

        // when
        when(memberRepository.findById(any())).thenReturn(Optional.of(mockMember));

        Member member = memberService.getBalance(memberId);

        // then
        assertThat(member.getBalance()).isEqualTo(balance);
    }

    @DisplayName("멤버의 잔액을 충전한다")
    @Test
    void chargeBalance() {
        // given
        Long memberId = 1L;
        Long currBalance = 1000L;
        Long chargeAmount = 5000L;

        Member mockMember = Member.builder()
                .balance(currBalance)
                .build();

        // when
        when(memberRepository.findById(any())).thenReturn(Optional.of(mockMember));

        MemberChargeRequest request = new MemberChargeRequest(memberId, chargeAmount);

        Member member = memberService.chargeBalance(request);

        // then
        assertThat(member.getBalance()).isEqualTo(currBalance + chargeAmount);
    }
}