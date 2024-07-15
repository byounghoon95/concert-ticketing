package com.example.concertticketing.domain.queue.infrastructure;

import com.example.concertticketing.domain.queue.model.Queue;
import com.example.concertticketing.domain.queue.model.QueueStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QueueJpaRepository extends JpaRepository<Queue, Long> {
    Optional<Queue> findByMemberIdAndStatusNot(@Param("memberId") Long memberId, @Param("status") QueueStatus status);

    @Modifying
    @Query("UPDATE Queue q SET q.status = 'EXPIRED', q.modifiedAt = :time WHERE q.expiredAt < :time AND q.status = 'ACTIVE'")
    void updateActiveTokenToExpired(@Param("time") LocalDateTime time);

    List<Queue> findByStatus(@Param("status") QueueStatus status, Pageable pageable);

    int countByStatus(@Param("status") QueueStatus status);

    Optional<Queue> findByMemberIdAndStatus(@Param("memberId") Long memberId, @Param("status") QueueStatus status);
}
