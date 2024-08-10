package com.example.concertticketing.interfaces.api.member;

import com.example.concertticketing.CommonControllerTest;
import com.example.concertticketing.interfaces.api.member.dto.MemberChargeRequest;
import com.example.concertticketing.domain.member.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberControllerTest extends CommonControllerTest {

    @DisplayName("특정 유저의 잔액을 조회한다")
    @Test
    void getBalance() throws Exception {
        // given
        Long memberId = 1L;
        Long balance = 5000L;

        Member member = Member.builder()
                .balance(balance)
                .build();

        // when
        when(memberService.getBalance(any())).thenReturn(member);

        // then
        mockMvc.perform(get("/api/member/balance/{memberId}", memberId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.balance").value(balance))
        ;
    }

    @DisplayName("특정 유저의 잔액을 충전한다")
    @Test
    void chargeBalance() throws Exception {
        // given
        Long memberId = 1L;
        Long balance = 5000L;
        MemberChargeRequest request = new MemberChargeRequest(memberId, balance);

        Member member = Member.builder()
                .balance(balance)
                .build();

        // when
        when(memberService.chargeBalance(any())).thenReturn(member);

        // then
        mockMvc.perform(post("/api/member/balance")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.balance").value(balance))
        ;
    }
}