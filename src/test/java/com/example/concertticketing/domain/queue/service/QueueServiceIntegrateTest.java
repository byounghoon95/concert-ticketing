package com.example.concertticketing.domain.queue.service;

import com.example.concertticketing.domain.member.model.Member;
import com.example.concertticketing.domain.member.repository.MemberRepository;
import com.example.concertticketing.domain.queue.repository.QueueRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * findFirstMemberId, findFirstQueueId
 * + 0 ~ 2L : 만료 토근 조회
 * + 2L ~ 5L : 활성 토큰 조회
 * + 6L ~ 8L : 대기 토큰 조회
 * */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QueueServiceIntegrateTest {
    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private QueueService queueService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager entityManager;

    void setUp() {
        for (int i = 0; i < 9; i++) {
            Member member = Member.builder()
                    .memberLoginId("A" + i)
                    .balance(Long.valueOf(i * 1000))
                    .build();

            memberRepository.save(member);
        }

        Long memberId = findFirstMemberId();

        for (int i = 6; i < 9; i++) {
            queueRepository.addWaitingQueue(memberId + i);
        }

        Set<String> activeSet = Set.of(
                memberId + 3L + ":" + LocalDateTime.of(2024, 6, 12, 0, 5, 0).toEpochSecond(ZoneOffset.UTC),
                memberId + 4L + ":" + LocalDateTime.of(2024, 6, 12, 0, 5, 0).toEpochSecond(ZoneOffset.UTC),
                memberId + 5L + ":" + LocalDateTime.of(2024, 6, 12, 0, 5, 0).toEpochSecond(ZoneOffset.UTC)
        );

        queueRepository.addActiveQueues(activeSet);
    }

    @AfterEach
    void tearDown() {
        queueRepository.flushAll();
        memberRepository.deleteAllInBatch();
    }

    Long findFirstMemberId() {
        return (Long) entityManager.createNativeQuery("SELECT id FROM MEMBER LIMIT 1")
                .getSingleResult();
    }

    @DisplayName("토큰이 유효한지 검증하고 유효하면 true 를 반환한다")
    @Test
    void verify_true() {
        // given
        setUp();
        Long memberId = findFirstMemberId() + 4L;

        // when
        boolean verify = queueService.verify(memberId);

        // then
        assertThat(verify).isEqualTo(true);
    }

    @DisplayName("토큰이 유효한지 검증하고 유효하지 않으면 false 를 반환한다")
    @Test
    void verify_false() {
        // given
        setUp();
        Long memberId = findFirstMemberId();

        // when
        boolean verify = queueService.verify(memberId);

        // then
        assertThat(verify).isEqualTo(false);
    }

    @DisplayName("활성 토큰 중 만료 기간이 지난 토큰을 만료시킨다")
    @Test
    void updateActiveTokenToExpired() {
        // given
        setUp();

        String key = findFirstMemberId() + 5L + ":" + LocalDateTime.of(2024, 6, 12, 0, 5, 0).toEpochSecond(ZoneOffset.UTC);
        LocalDateTime now = LocalDateTime.of(2024, 6, 12, 0, 6, 0);

        // when
        boolean prev = queueRepository.isInActiveTokens(key);
        queueService.updateActiveTokenToExpired(now);
        boolean curr = queueRepository.isInActiveTokens(key);


        // then
        assertThat(prev).isEqualTo(true);
        assertThat(curr).isEqualTo(false);
    }

    @DisplayName("활성 토큰 중 만료 기간이 지나지 않았으면 작업을 하지 않는다")
    @Test
    void updateActiveTokenToExpired_fail() {
        // given
        setUp();

        String key = findFirstMemberId() + 5L + ":" + LocalDateTime.of(2024, 6, 12, 0, 5, 0).toEpochSecond(ZoneOffset.UTC);
        LocalDateTime now = LocalDateTime.of(2024, 6, 12, 0, 4, 0);

        // when
        boolean prev = queueRepository.isInActiveTokens(key);
        queueService.updateActiveTokenToExpired(now);
        boolean curr = queueRepository.isInActiveTokens(key);

        // then
        assertThat(prev).isEqualTo(true);
        assertThat(curr).isEqualTo(true);
    }

    @DisplayName("대기중인 토큰을 활성상태로 변경한다")
    @Test
    void updateWaitTokenToActive() {
        // given
        setUp();
        Long memberId = findFirstMemberId() + 8L;
        LocalDateTime now = LocalDateTime.of(2024, 6, 12, 0, 5, 0);

        // when
        boolean prev = queueRepository.isInWaitingTokens(memberId);
        // 입장 가능한 인원만큼 변경 가능한데 현재 ACTIVE 인원이 3이고
        // queueId가 WAIT 의 마지막이라 3명이 더 입장가능한 available 값을 설정
        queueService.updateWaitTokenToActive(now,6);
        boolean curr = queueRepository.isInWaitingTokens(memberId);

        // then
        assertThat(prev).isEqualTo(true);
        assertThat(curr).isEqualTo(false);
    }
}
