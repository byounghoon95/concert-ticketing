package com.example.concertticketing.domain.queue.repository;

import com.example.concertticketing.domain.queue.infrastructure.QueueJpaRepository;
import com.example.concertticketing.domain.queue.model.Queue;
import com.example.concertticketing.domain.queue.model.QueueStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class QueueRepositoryImpl implements QueueRepository {

    private final QueueJpaRepository queueJpaRepository;

    @Override
    public Queue save(Queue queue) {
        return queueJpaRepository.save(queue);
    }

    @Override
    public Optional<Queue> findValidTokenByMemberId(Long memberId, QueueStatus status) {
        return queueJpaRepository.findByMemberIdAndStatusNot(memberId, status);
    }

    @Override
    public void updateActiveTokenToExpired(LocalDateTime time) {
        queueJpaRepository.updateActiveTokenToExpired(time);
    }

    @Override
    public List<Queue> findFirstWaitMember(QueueStatus status, Pageable pageable) {
        return queueJpaRepository.findByStatus(status, pageable);
    }

    @Override
    public int countActiveMember(QueueStatus status) {
        return queueJpaRepository.countByStatus(status);
    }

    @Override
    public List<Queue> findWaitMemberList(QueueStatus prev, PageRequest pageable) {
        return queueJpaRepository.findByStatus(prev, pageable);
    }

    @Override
    public Optional<Queue> findActiveTokenByMemberId(Long memberId, QueueStatus status) {
        return queueJpaRepository.findByMemberIdAndStatus(memberId, status);
    }

    @Override
    public void deleteAllInBatch() {
        queueJpaRepository.deleteAllInBatch();
    }

    @Override
    public Optional<Queue> findById(Long id) {
        return queueJpaRepository.findById(id);
    }
}
