package com.example.concertticketing.domain.member.service;

import com.example.concertticketing.api.member.dto.MemberChargeRequest;
import com.example.concertticketing.domain.member.model.Member;
import com.example.concertticketing.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberServiceIntegrateTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberServiceImpl memberService;

    @Autowired
    private EntityManager entityManager;

    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    private void setUp() {
        Member member = createMember("A1", 5000L);
        memberRepository.save(member);
    }

    private Member createMember(String loginId, Long balance) {
        return Member.builder()
                .memberLoginId(loginId)
                .balance(balance)
                .build();
    }

    protected Member findFirstMember() {
        return (Member) entityManager.createNativeQuery("SELECT * FROM MEMBER LIMIT 1", Member.class)
                .getSingleResult();
    }

    @DisplayName("동시에 3개의 충전 요청이 들어오고 하나만 성공한다")
    @Test
    void chargeBalance() {
        // given
        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> setUp())
        ).join();

        Member member = findFirstMember();
        Long memberId = member.getId();
        Long chargeAmount = 5000L;

        MemberChargeRequest request = new MemberChargeRequest(memberId, chargeAmount);

        // when
        List<CompletableFuture<Void>> futures =
                List.of(
                        CompletableFuture.runAsync(() -> memberService.chargeBalance(request)),
                        CompletableFuture.runAsync(() -> memberService.chargeBalance(request)),
                        CompletableFuture.runAsync(() -> memberService.chargeBalance(request))
                );

        futures.stream()
                .forEach(future -> {
                    try {
                        future.join();
                    } catch (Exception e) {
                        System.out.println("Error : " + e.getMessage());
                    }
                });

        Member updatedMember = memberRepository.findById(memberId).get();

        // then
        assertThat(updatedMember.getBalance()).isEqualTo(member.getBalance() + chargeAmount);

        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> tearDown())
        ).join();
    }
}