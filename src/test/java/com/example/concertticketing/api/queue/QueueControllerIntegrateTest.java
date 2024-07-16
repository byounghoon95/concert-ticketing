package com.example.concertticketing.api.queue;

import com.example.concertticketing.CommonControllerIntegrateTest;
import com.example.concertticketing.domain.exception.CustomException;
import com.example.concertticketing.domain.exception.ErrorEnum;
import com.example.concertticketing.domain.queue.model.Queue;
import com.example.concertticketing.domain.queue.model.QueueStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * findFirstMemberId, findFirstQueueId
 * + 0 ~ 2L : 만료 토근 조회
 * + 2L ~ 5L : 활성 토큰 조회
 * + 6L ~ 8L : 대기 토큰 조회
 * */
public class QueueControllerIntegrateTest extends CommonControllerIntegrateTest {
    @AfterEach
    void tearDown() {
        queueRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("대기중인 사람이 없는 상태에서 토큰을 발급한다")
    @Test
    void enqueue_no_wait_member() {
        // given
        Long memberId = 1L;

        // when
        Queue queue = queueService.enqueue(memberId);

        // then
        assertThat(queue.getId()).isEqualTo(1L);
        assertThat(queue.getToken()).isInstanceOf(UUID.class);
        assertThat(queue.getStatus()).isEqualTo(QueueStatus.WAIT);
        assertThat(queue.getPosition()).isEqualTo(0L);
    }

    @DisplayName("대기중인 사람이 있고 새로운 유저가 토큰을 발급한다")
    @Test
    void enqueue_wait_member_exist() {
        // given
        setUpQueue();
        Long memberId = findFirstMemberId() + 10L;

        // when
        Queue queue = queueService.enqueue(memberId);

        // then
        assertThat(queue.getToken()).isInstanceOf(UUID.class);
        assertThat(queue.getStatus()).isEqualTo(QueueStatus.WAIT);
        assertThat(queue.getPosition()).isEqualTo(3L);
    }

    @DisplayName("토큰이 만료되지 않은 유저가 토큰을 재발급하면 원래 존재하는 토큰을 반환한다")
    @Test
    void enqueue_already_get_token() {
        // given
        setUpQueue();
        Long memberId = findFirstMemberId() + 5L;

        // when
        Queue myQueue = queueRepository.findValidTokenByMemberId(memberId, QueueStatus.EXPIRED).get();
        Queue newQueue = queueService.enqueue(memberId);

        // then
        assertThat(myQueue.getToken()).isEqualTo(newQueue.getToken());
        assertThat(myQueue.getStatus()).isEqualTo(newQueue.getStatus());
        assertThat(newQueue.getPosition()).isEqualTo(0L);
        assertThat(myQueue.getMember().getId()).isEqualTo(newQueue.getMember().getId());
    }

    @DisplayName("active 상태인 현재 나의 토큰 정보를 반환한다")
    @Test
    void getInfo_active() {
        // given
        setUpQueue();
        Long memberId = findFirstMemberId() + 5L;

        // when
        Queue myQueue = queueRepository.findValidTokenByMemberId(memberId, QueueStatus.EXPIRED).get();
        Queue newQueue = queueService.getInfo(memberId);

        // then
        assertThat(myQueue.getToken()).isEqualTo(newQueue.getToken());
        assertThat(myQueue.getStatus()).isEqualTo(newQueue.getStatus());
        assertThat(newQueue.getPosition()).isEqualTo(0L);
        assertThat(myQueue.getMember().getId()).isEqualTo(newQueue.getMember().getId());
    }

    @DisplayName("wait 상태인 현재 나의 토큰 정보를 반환한다")
    @Test
    void getInfo_wait() {
        // given
        setUpQueue();
        Long memberId = findFirstMemberId() + 8L;

        // when
        Queue myQueue = queueRepository.findValidTokenByMemberId(memberId, QueueStatus.EXPIRED).get();
        Queue newQueue = queueService.getInfo(memberId);

        // then
        assertThat(myQueue.getToken()).isEqualTo(newQueue.getToken());
        assertThat(myQueue.getStatus()).isEqualTo(newQueue.getStatus());
        assertThat(newQueue.getPosition()).isEqualTo(2L);
        assertThat(myQueue.getMember().getId()).isEqualTo(newQueue.getMember().getId());
    }

    @DisplayName("토큰 정보 조회 시 토큰이 만료되어 에러를 반환한다")
    @Test
    void getInfo_token_expired() {
        // given
        setUpQueue();
        Long memberId = findFirstMemberId();

        // when then
        assertThatThrownBy(() -> queueService.getInfo(memberId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorEnum.TOKEN_EXPIRED.getMessage());
    }
}
