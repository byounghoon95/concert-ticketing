package com.example.concertticketing.domain.queue.service;

import com.example.concertticketing.domain.exception.CustomException;
import com.example.concertticketing.domain.exception.ErrorEnum;
import com.example.concertticketing.domain.member.model.Member;
import com.example.concertticketing.domain.member.repository.MemberRepository;
import com.example.concertticketing.domain.member.service.MemberService;
import com.example.concertticketing.domain.queue.model.Queue;
import com.example.concertticketing.domain.queue.model.QueueStatus;
import com.example.concertticketing.domain.queue.repository.QueueRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * findFirstMemberId, findFirstQueueId
 * + 0 ~ 2L : 만료 토근 조회
 * + 2L ~ 5L : 활성 토큰 조회
 * + 6L ~ 8L : 대기 토큰 조회
 * */
@SpringBootTest
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

            QueueStatus status;
            LocalDateTime time = null;
            if (i < 3) {
                status = QueueStatus.EXPIRED;
                time = LocalDateTime.of(2024, 6, 12, 0, 0, 0);
            } else if (i < 6) {
                status = QueueStatus.ACTIVE;
                time = LocalDateTime.of(2024, 6, 12, 0, 5, 0);
            } else {
                status = QueueStatus.WAIT;
            }

            Queue queue = Queue.builder()
                    .token(UUID.randomUUID())
                    .member(member)
                    .expiredAt(time)
                    .status(status)
                    .build();
            queueRepository.save(queue);
        }
    }

    @AfterEach
    void tearDown() {
        queueRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    Long findFirstQueueId() {
        return (Long) entityManager.createNativeQuery("SELECT id FROM QUEUE LIMIT 1")
                .getSingleResult();
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
        Long memberId = findFirstMemberId() + 5L;

        // when
        boolean verify = queueService.verify(memberId);

        // then
        assertThat(verify).isEqualTo(true);
    }

    @DisplayName("토큰이 유효한지 검증하고 유효하지 않으면  false 를 반환한다")
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
        Long queueId = findFirstQueueId() + 5L;
        LocalDateTime now = LocalDateTime.of(2024, 6, 12, 0, 6, 0);

        // when
        Queue prevQueue = queueRepository.findById(queueId).get();
        queueService.updateActiveTokenToExpired(now);
        Queue currQueue = queueRepository.findById(queueId).get();

        // then
        assertThat(prevQueue.getStatus()).isEqualTo(QueueStatus.ACTIVE);
        assertThat(currQueue.getStatus()).isEqualTo(QueueStatus.EXPIRED);
    }

    @DisplayName("활성 토큰 중 만료 기간이 지나지 않았으면 작업을 하지 않는다")
    @Test
    void updateActiveTokenToExpired_fail() {
        // given
        setUp();
        Long queueId = findFirstQueueId() + 5L;
        LocalDateTime now = LocalDateTime.of(2024, 6, 12, 0, 4, 0);

        // when
        Queue prevQueue = queueRepository.findById(queueId).get();
        queueService.updateActiveTokenToExpired(now);
        Queue currQueue = queueRepository.findById(queueId).get();

        // then
        assertThat(prevQueue.getStatus()).isEqualTo(QueueStatus.ACTIVE);
        assertThat(currQueue.getStatus()).isEqualTo(QueueStatus.ACTIVE);
    }

    @DisplayName("대기중인 토큰을 활성상태로 변경한다")
    @Test
    void updateWaitTokenToActive() {
        // given
        setUp();
        Long queueId = findFirstQueueId() + 8L;
        LocalDateTime now = LocalDateTime.of(2024, 6, 12, 0, 5, 0);

        // when
        Queue prevQueue = queueRepository.findById(queueId).get();
        // 입장 가능한 인원만큼 변경 가능한데 현재 ACTIVE 인원이 3이고
        // queueId가 WAIT 의 마지막이라 3명이 더 입장가능한 available 값을 설정
        queueService.updateWaitTokenToActive(now,6);
        Queue currQueue = queueRepository.findById(queueId).get();

        // then
        assertThat(prevQueue.getStatus()).isEqualTo(QueueStatus.WAIT);
        assertThat(currQueue.getStatus()).isEqualTo(QueueStatus.ACTIVE);
        assertThat(currQueue.getExpiredAt()).isEqualTo(now.plusSeconds(10));
    }

    @DisplayName("대기중인 토큰을 활성상태로 변경 시 최대 활성가능 인원이 다 찼으면 작업을 하지 않는다")
    @Test
    void updateWaitTokenToActive_fail() {
        // given
        setUp();
        LocalDateTime now = LocalDateTime.of(2024, 6, 12, 0, 5, 0);

        // when then
        assertThatThrownBy(() -> queueService.updateWaitTokenToActive(now,3))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorEnum.NO_MORE_ACTIVE_TOKEN.getMessage());
    }

//    @DisplayName("활성 상태의 토큰을 만료시킨다")
//    @Test
//    void expiredToken() {
//        // given
//        setUp();
//        Long queueId = findFirstQueueId() + 5L;
//
//        // when
//        Queue prevQueue = queueRepository.findById(queueId).get();
//        queueService.expiredToken(queueId, QueueStatus.EXPIRED);
//        Queue currQueue = queueRepository.findById(queueId).get();
//
//        // then
//        assertThat(prevQueue.getStatus()).isEqualTo(QueueStatus.ACTIVE);
//        assertThat(currQueue.getStatus()).isEqualTo(QueueStatus.EXPIRED);
//    }
//
//    @DisplayName("활성상태의 토큰을 만료시키는데 이미 만료되었다면 에러를 반환한다")
//    @Test
//    void expiredToken_already_expired() {
//        // given
//        setUp();
//        Long queueId = findFirstQueueId();
//
//        // when then
//        assertThatThrownBy(() -> queueService.expiredToken(queueId,QueueStatus.EXPIRED))
//                .isInstanceOf(CustomException.class)
//                .hasMessage(ErrorEnum.TOKEN_EXPIRED.getMessage());
//    }
}
