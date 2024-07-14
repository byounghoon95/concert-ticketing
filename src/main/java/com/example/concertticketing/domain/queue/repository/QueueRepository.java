package com.example.concertticketing.domain.queue.repository;


import com.example.concertticketing.domain.queue.model.Queue;
import com.example.concertticketing.domain.queue.model.QueueStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QueueRepository {
    Queue save(Queue queue);

    Optional<Queue> findValidTokenByMemberId(Long memberId, QueueStatus status);

    void updateActiveTokenToExpired(LocalDateTime time);

    List<Queue> findFirstWaitMember(QueueStatus status, Pageable pageable);

    int countActiveMember(QueueStatus status);

    List<Queue> findWaitMemberList(QueueStatus prev, PageRequest pageable);

    Optional<Queue> findActiveTokenByMemberId(Long memberId, QueueStatus status);
}
