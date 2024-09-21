package com.example.memberservice.interfaces;

import com.example.memberservice.domain.model.Member;
import com.example.memberservice.domain.service.MemberServiceImpl;
import com.example.memberservice.interfaces.api.dto.MemberChargeRequest;
import com.example.memberservice.util.SlackClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberServiceImpl memberService;

    @MockBean
    private SlackClient slackClient;

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