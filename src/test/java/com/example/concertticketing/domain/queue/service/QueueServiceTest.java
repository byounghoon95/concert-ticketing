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
        UUID uuid = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        Long firstLoc = 3L;
        Long myLoc = 6L;
        Long position = myLoc - firstLoc;

        Member member = Member.builder()
                .build();

        Queue waitQueue = Queue.builder()
                .id(firstLoc)
                .token(uuid)
                .member(member)
                .status(QueueStatus.WAIT)
                .build();

        Queue myQueue = Queue.builder()
                .id(myLoc)
                .token(uuid2)
                .member(member)
                .status(QueueStatus.WAIT)
                .build();

        // when
        when(memberService.getReferenceById(any())).thenReturn(member);
        when(queueRepository.findValidTokenByMemberId(any(), any())).thenReturn(Optional.empty());
        when(queueRepository.findFirstWaitMember(any(), any())).thenReturn(List.of(waitQueue));
        when(queueRepository.save(any())).thenReturn(myQueue);

        Queue queue = queueService.enqueue(memberId);

        // then
        assertThat(queue.getStatus()).isEqualTo(QueueStatus.WAIT);
        assertThat(queue.getToken()).isInstanceOf(UUID.class);
        assertThat(queue.getPosition()).isEqualTo(position);
    }

    @DisplayName("유저가 발급받은 토큰이 존재해 원래 토큰을 반환하고 그때의 순서는 0이다")
    @Test
    void enqueue_already_get_token() {
        // given
        Long memberId = 1L;
        UUID uuid = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        Long firstLoc = 6L;
        Long myLoc = 3L;

        Member member = Member.builder()
                .build();

        Queue waitQueue = Queue.builder()
                .id(firstLoc)
                .token(uuid)
                .member(member)
                .status(QueueStatus.WAIT)
                .build();

        Queue myQueue = Queue.builder()
                .id(myLoc)
                .token(uuid2)
                .member(member)
                .status(QueueStatus.ACTIVE)
                .build();

        // when
        when(memberService.getReferenceById(any())).thenReturn(member);
        when(queueRepository.findValidTokenByMemberId(any(), any())).thenReturn(Optional.of(myQueue));
        when(queueRepository.findFirstWaitMember(any(), any())).thenReturn(List.of(waitQueue));

        Queue queue = queueService.enqueue(memberId);

        // then
        assertThat(queue.getStatus()).isEqualTo(QueueStatus.ACTIVE);
        assertThat(queue.getToken()).isInstanceOf(UUID.class);
        assertThat(queue.getPosition()).isEqualTo(0);
    }

    @DisplayName("토큰 발급 시 대기하고 있는 멤버가 없어 순서 0을 반환한다")
    @Test
    void enqueue_no_wait_member() {
        // given
        Long memberId = 1L;
        UUID uuid = UUID.randomUUID();

        Member member = Member.builder()
                .build();

        Queue myQueue = Queue.builder()
                .id(1L)
                .token(uuid)
                .member(member)
                .status(QueueStatus.WAIT)
                .build();

        // when
        when(memberService.getReferenceById(any())).thenReturn(member);
        when(queueRepository.findValidTokenByMemberId(any(), any())).thenReturn(Optional.of(myQueue));
        when(queueRepository.findFirstWaitMember(any(), any())).thenReturn(List.of());

        Queue queue = queueService.enqueue(memberId);

        // then
        assertThat(queue.getStatus()).isEqualTo(QueueStatus.WAIT);
        assertThat(queue.getToken()).isInstanceOf(UUID.class);
        assertThat(queue.getPosition()).isEqualTo(0);
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

    @DisplayName("유효한 토큰이 존재하면 true 를 반환한다")
    @Test
    void verify_true() {
        Long memberId = 1L;
        UUID uuid = UUID.randomUUID();
        Long position = 1L;
        Queue queue = Queue.builder()
                .id(position)
                .token(uuid)
                .status(QueueStatus.WAIT)
                .build();

        when(queueRepository.findValidTokenByMemberId(any(),any())).thenReturn(Optional.of(queue));

        boolean verification = queueService.verify(memberId);

        // then
        assertThat(verification).isEqualTo(true);
    }

    @DisplayName("유효한 토큰이 존재하지 않으면 false 를 반환한다")
    @Test
    void verify_false() {
        // given
        Long memberId = 1L;

        // when
        when(queueRepository.findValidTokenByMemberId(any(),any())).thenReturn(Optional.empty());

        boolean verification = queueService.verify(memberId);

        // then
        assertThat(verification).isEqualTo(false);
    }
}