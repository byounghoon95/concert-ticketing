package com.example.concertticketing.domain.queue.service;

import com.example.concertticketing.domain.member.model.Member;
import com.example.concertticketing.domain.member.service.MemberService;
import com.example.concertticketing.domain.queue.model.Queue;
import com.example.concertticketing.domain.queue.model.QueueStatus;
import com.example.concertticketing.domain.queue.repository.QueueRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {
    @Mock
    private QueueRepository queueRepository;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private QueueServiceImpl queueService;

    @DisplayName("유저가 발급받은 토큰이 없어 새로운 토큰을 발급한다")
    @Test
    void enqueue() {
        // given
        Long memberId = 1L;
        String memberLoginId = "A1";
        Long balance = 5000L;
        UUID uuid = UUID.randomUUID();

        Member member = Member.builder()
                .memberLoginId(memberLoginId)
                .balance(balance)
                .build();

        Queue waitQueue = Queue.builder()
                .token(uuid)
                .member(member)
                .status(QueueStatus.WAIT)
                .build();

        // when
        when(memberService.getReferenceById(any())).thenReturn(member);
        when(queueRepository.findValidTokenByMemberId(any(), any())).thenReturn(Optional.empty());
        when(queueRepository.save(any())).thenReturn(waitQueue);

        Queue queue = queueService.enqueue(memberId);

        // then
        assertThat(queue.getStatus()).isEqualTo(QueueStatus.WAIT);
        assertThat(queue.getToken()).isInstanceOf(UUID.class);
    }

    @DisplayName("유저가 발급받은 토큰이 존재해 원래 토큰을 반환한다")
    @Test
    void enqueue_already_get_token() {
        // given
        Long memberId = 1L;
        String memberLoginId = "A1";
        Long balance = 5000L;
        UUID uuid = UUID.randomUUID();

        Member member = Member.builder()
                .memberLoginId(memberLoginId)
                .balance(balance)
                .build();

        Queue waitQueue = Queue.builder()
                .token(uuid)
                .member(member)
                .status(QueueStatus.ACTIVE)
                .build();

        // when
        when(memberService.getReferenceById(any())).thenReturn(member);
        when(queueRepository.findValidTokenByMemberId(any(), any())).thenReturn(Optional.of(waitQueue));

        Queue queue = queueService.enqueue(memberId);

        // then
        assertThat(queue.getStatus()).isEqualTo(QueueStatus.ACTIVE);
        assertThat(queue.getToken()).isInstanceOf(UUID.class);
    }

    @DisplayName("유저의 현재 대기열 정보를 반환한다")
    @Test
    void getInfo() {
        // given
        Long memberId = 1L;
        UUID uuid = UUID.randomUUID();
        Long firstPosition = 1L;
        Long myPosition = 5L;

        Queue first = Queue.builder()
                .id(firstPosition)
                .status(QueueStatus.WAIT)
                .build();
        Queue myQueue = Queue.builder()
                .id(myPosition)
                .token(uuid)
                .status(QueueStatus.WAIT)
                .build();
        List<Queue> firstQueue = List.of(first);

        // when
        when(queueRepository.findFirstWaitMember(any(),any())).thenReturn(firstQueue);
        when(queueRepository.findValidTokenByMemberId(any(), any())).thenReturn(Optional.of(myQueue));

        Queue queue = queueService.getInfo(memberId);

        // then
        assertThat(queue.getPosition()).isEqualTo(myPosition - firstPosition);
        assertThat(queue.getToken()).isInstanceOf(UUID.class);
    }
}