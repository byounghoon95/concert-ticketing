package com.example.concertticketing.domain.queue.infrastructure;

import com.example.concertticketing.domain.queue.model.Queue;
import com.example.concertticketing.domain.queue.model.QueueStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QueueJpaRepository extends JpaRepository<Queue, UUID> {
    @Query("SELECT q FROM Queue q WHERE q.member.id = :memberId AND q.status != :status")
    Optional<Queue> findValidTokenByMemberId(@Param("memberId") Long memberId, @Param("status") QueueStatus status);

    @Modifying
    @Query("UPDATE Queue q SET q.status = :change WHERE q.expiredAt < :time AND q.status = :prev")
    void updateActiveTokenToExpired(@Param("time") LocalDateTime time, @Param("prev") QueueStatus prev, @Param("change") QueueStatus change);

    @Query("SELECT q FROM Queue q WHERE q.status = :status")
    List<Queue> findFirstWaitMember(@Param("status") QueueStatus status, Pageable pageable);

    @Query("SELECT COUNT(q) FROM Queue q WHERE q.status = :status")
    int countActiveMember(@Param("status") QueueStatus status);

    @Query("SELECT q FROM Queue q WHERE q.status = :status")
    List<Queue> findWaitMemberList(@Param("status") QueueStatus status, PageRequest pageable);

    @Query("SELECT q FROM Queue q WHERE q.member.id = :memberId AND q.status = :status")
    Optional<Queue> findActiveTokenByMemberId(Long memberId, QueueStatus status);
}
