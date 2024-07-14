package com.example.concertticketing.domain.queue.service;

import com.example.concertticketing.domain.member.model.Member;
import com.example.concertticketing.domain.member.service.MemberService;
import com.example.concertticketing.domain.queue.model.Queue;
import com.example.concertticketing.domain.queue.model.QueueStatus;
import com.example.concertticketing.domain.queue.repository.QueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class QueueServiceImpl implements QueueService {

    private final QueueRepository queueRepository;
    private final MemberService memberService;

    /**
     * 토큰 발급 시 이미 발급된 토큰이면
     * 새로 발급없이 원래 토큰 반환
     * */
    @Transactional
    @Override
    public Queue enqueue(Long memberId) {
        Member member = memberService.getReferenceById(memberId);
        Queue myQueue = queueRepository.findValidTokenByMemberId(memberId, QueueStatus.EXPIRED)
                .orElseGet(() -> queueRepository.save(createWaitQueue(member)));

        List<Queue> queueList = queueRepository.findFirstWaitMember(QueueStatus.WAIT,PageRequest.of(0, 1));
        Long position = queueList.size() > 0 ? myQueue.getId() - queueList.get(0).getId() : 0;

        myQueue.updatePosition(position);
        return myQueue;
    }

    /**
     * 유효한 토큰이 존재하면 true, 존재하지 않으면 false
     * */
    public boolean verify(Long memberId) {
        return queueRepository.findValidTokenByMemberId(memberId, QueueStatus.EXPIRED).isPresent();
    }

    /**
     * ACTIVE 인 토큰의 상태를 EXPIRED 로 변경
     * prev : ACTIVE
     * change : EXPIRED
     * */
    @Transactional
    @Override
    public void updateActiveTokenToExpired(LocalDateTime time) {
        queueRepository.updateActiveTokenToExpired(time);
    }

    @Override
    public Queue getInfo(Long memberId) {
        List<Queue> queueList = queueRepository.findFirstWaitMember(QueueStatus.WAIT,PageRequest.of(0, 1));
        Queue myQueue = queueRepository.findValidTokenByMemberId(memberId, QueueStatus.EXPIRED)
                .orElseThrow(() -> new NullPointerException("유효한 토큰이 없습니다"));

        Long position = queueList.size() > 0 ? myQueue.getId() - queueList.get(0).getId() : 0;
        myQueue.updatePosition(position);
        return myQueue;
    }

    /**
     * 동시 접속 가능한 인원은 10명 이라고 가정
     * prev : WAIT
     * change : ACTIVE
     * */
    @Transactional
    @Override
    public void updateWaitTokenToActive(LocalDateTime now) {
        int available = 10;
        int count = queueRepository.countActiveMember(QueueStatus.ACTIVE);

        if (count == available) {
            return;
        }

        queueRepository.findWaitMemberList(QueueStatus.WAIT, PageRequest.of(0, available - count)).stream()
                .forEach(queue -> {
                    queue.updateStatus(QueueStatus.ACTIVE);
                    queue.updateExpiredAt(LocalDateTime.now().plusSeconds(10));
                });
    }

    public void expiredToken(Long memberId, QueueStatus status) {
        Queue token = queueRepository.findActiveTokenByMemberId(memberId, QueueStatus.ACTIVE)
                .orElseThrow(() -> new NullPointerException("유효한 토큰이 없습니다"));
        token.updateStatus(status);
    }

    private Queue createWaitQueue(Member member) {
        return Queue.builder()
                .token(UUID.randomUUID())
                .member(member)
                .status(QueueStatus.WAIT)
                .build();
    }
}
