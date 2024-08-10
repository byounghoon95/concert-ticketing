package com.example.concertticketing.interfaces.api.member;

import com.example.concertticketing.CommonControllerIntegrateTest;
import com.example.concertticketing.interfaces.api.common.response.CommonResponse;
import com.example.concertticketing.interfaces.api.member.dto.MemberChargeRequest;
import com.example.concertticketing.interfaces.api.member.dto.MemberResponse;
import com.example.concertticketing.exception.ErrorEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberControllerIntegrateTest extends CommonControllerIntegrateTest {
    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("현재 잔액을 조회한다")
    @Test
    void getBalance() {
        // given
        setUpMember();
        Long memberId = findFirstMemberId();
        String url = "http://localhost:" + port + "/api/member/balance/" + memberId;

        // when
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> header = new HttpEntity<>(headers);
        ResponseEntity<CommonResponse<MemberResponse>> response = restTemplate.exchange(url, HttpMethod.GET, header, new ParameterizedTypeReference<>() {});
        CommonResponse<MemberResponse> body = response.getBody();
        MemberResponse data = body.getData();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.SUCCESS.getMessage());
        assertThat(data.getBalance()).isEqualTo(5000);
    }

    @DisplayName("현재 잔액을 조회 시 멤버가 존재하지 않아 에러 발생한다")
    @Test
    void getBalance_no_member() {
        // given
        setUpMember();
        Long memberId = findFirstMemberId() + 1;
        String url = "http://localhost:" + port + "/api/member/balance/" + memberId;

        // when
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> header = new HttpEntity<>(headers);
        ResponseEntity<CommonResponse<MemberResponse>> response = restTemplate.exchange(url, HttpMethod.GET, header, new ParameterizedTypeReference<>() {});
        CommonResponse<MemberResponse> body = response.getBody();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.MEMBER_NOT_FOUND.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.MEMBER_NOT_FOUND.getMessage());
    }

    @DisplayName("현재 잔액을 충전한다")
    @Test
    void chargeBalance() {
        // given
        setUpMember();
        Long memberId = findFirstMemberId();
        String url = "http://localhost:" + port + "/api/member/balance";

        MemberChargeRequest request = new MemberChargeRequest(memberId, 5000L);

        // when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MemberChargeRequest> header = new HttpEntity<>(request,headers);

        ResponseEntity<CommonResponse<MemberResponse>> response = restTemplate.exchange(url, HttpMethod.POST, header, new ParameterizedTypeReference<>() {});
        CommonResponse<MemberResponse> body = response.getBody();
        MemberResponse data = body.getData();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.SUCCESS.getMessage());
        assertThat(data.getBalance()).isEqualTo(10000L);
    }

    @DisplayName("현재 잔액을 조회 시 멤버가 존재하지 않아 에러 발생한다")
    @Test
    void chargeBalance_no_member() {
        // given
        setUpMember();
        Long memberId = findFirstMemberId() + 1;
        String url = "http://localhost:" + port + "/api/member/balance";

        MemberChargeRequest request = new MemberChargeRequest(memberId, 5000L);

        // when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MemberChargeRequest> header = new HttpEntity<>(request,headers);
        ResponseEntity<CommonResponse<MemberResponse>> response = restTemplate.exchange(url, HttpMethod.POST, header, new ParameterizedTypeReference<>() {});
        CommonResponse<MemberResponse> body = response.getBody();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.MEMBER_NOT_FOUND.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.MEMBER_NOT_FOUND.getMessage());
    }
}
