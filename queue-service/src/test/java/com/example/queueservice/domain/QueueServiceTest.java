package com.example.queueservice.domain;

import com.example.queueservice.domain.model.ActiveQueue;
import com.example.queueservice.domain.model.Queue;
import com.example.queueservice.domain.repository.QueueRepository;
import com.example.queueservice.domain.service.QueueServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class QueueServiceTest {
    @Mock
    private QueueRepository queueRepository;

    @InjectMocks
    private QueueServiceImpl queueService;

    @DisplayName("유저가 발급받은 토큰이 없어 새로운 토큰을 발급한다")
    @Test
    void enqueue() {
        // given
        Long memberId = 1L;
        Long firstLoc = 3L;
        Long myLoc = 6L;
        Long position = myLoc - firstLoc;

        // when
        when(queueRepository.isInWaitingTokens(any())).thenReturn(false);
        when(queueRepository.getPosition(any())).thenReturn(position);

        Queue queue = queueService.enqueue(memberId);

        // then
        assertThat(queue.getMemberId()).isEqualTo(memberId);
        assertThat(queue.getPosition()).isEqualTo(position);
    }

    @DisplayName("유저의 현재 대기열 정보를 반환한다")
    @Test
    void getInfo() {
        // given
        Long memberId = 1L;
        Long firstPosition = 1L;
        Long myPosition = 5L;
        Long position = myPosition - firstPosition;

        // when
        when(queueRepository.isInWaitingTokens(any())).thenReturn(true);
        when(queueRepository.getPosition(any())).thenReturn(position);
        Queue queue = queueService.getInfo(memberId);

        // then
        assertThat(queue.getPosition()).isEqualTo(position);
    }

    @DisplayName("유효한 토큰이 존재하면 true 를 반환한다")
    @Test
    void verify_true() {
        // given
        Long memberId = 1L;
        Set<ActiveQueue> tokens = Set.of(
                new ActiveQueue(String.valueOf(memberId), "1234555")
        );

        // when
        when(queueRepository.getActiveTokens()).thenReturn(tokens);

        boolean verification = queueService.verify(memberId);

        // then
        assertThat(verification).isEqualTo(true);
    }

    @DisplayName("유효한 토큰이 존재하지 않으면 false 를 반환한다")
    @Test
    void verify_false() {
        // given
        Long memberId = 1L;
        Set<ActiveQueue> tokens = Set.of(
                new ActiveQueue(String.valueOf(memberId + 1), "1234555")
        );

        // when
        when(queueRepository.getActiveTokens()).thenReturn(tokens);

        boolean verification = queueService.verify(memberId);

        // then
        assertThat(verification).isEqualTo(false);
    }
}