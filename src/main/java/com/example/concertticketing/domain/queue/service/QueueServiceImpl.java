package com.example.concertticketing.domain.queue.service;

import com.example.concertticketing.exception.CustomException;
import com.example.concertticketing.exception.ErrorEnum;
import com.example.concertticketing.domain.queue.model.ActiveQueue;
import com.example.concertticketing.domain.queue.model.Queue;
import com.example.concertticketing.domain.queue.repository.QueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class QueueServiceImpl implements QueueService {

    private final QueueRepository queueRepository;

    @Transactional
    @Override
    public Queue enqueue(Long memberId) {
        boolean inWait = queueRepository.isInWaitingTokens(memberId);
        if (!inWait) {
            queueRepository.addWaitingQueue(memberId);
        }

        Long position = queueRepository.getPosition(memberId);

        return new Queue(memberId,position);
    }

    /**
     * 유효한 토큰(active)이 존재하면 true, 존재하지 않으면 false
     * */
    public boolean verify(Long memberId) {
        Set<ActiveQueue> tokens = queueRepository.getActiveTokens();
        if (tokens.isEmpty()) {
            return false;
        }

        for (ActiveQueue token : tokens) {
            if (token.compareTokenKey(memberId)) {
                return true;
            }
        }

        return false;
    }

    @Transactional
    @Override
    public void expireActiveTokens(LocalDateTime time) {
        Set<ActiveQueue> tokens = queueRepository.getActiveTokens();
        if (tokens.isEmpty()) {
            return;
        }

        tokens.forEach(token -> {
            if (token.isExpired(time)) {
                queueRepository.removeActiveQueue(token.makeActiveKey());
            }
        });
    }

    @Transactional
    @Override
    public Queue getInfo(Long memberId) {
        boolean inWait = queueRepository.isInWaitingTokens(memberId);
        if (!inWait) {
            throw new CustomException(ErrorEnum.NO_WAIT_TOKEN);
        }

        Long position = queueRepository.getPosition(memberId);

        return new Queue(memberId, position);
    }

    @Transactional
    @Override
    public void updateWaitTokenToActive(LocalDateTime now, int available) {
        Set<String> tokens = queueRepository.getWaitTokens(available);
        if (tokens.isEmpty()) {
            return;
        }
        
        // active 상태 5분간 유지
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(5);
        Set<String> activeMembers = tokens.stream()
                .map(memberId -> memberId + ":" + expiredAt.toEpochSecond(ZoneOffset.UTC))
                .collect(Collectors.toSet());

        queueRepository.removeWaitQueues(tokens);
        queueRepository.addActiveQueues(activeMembers);
    }

    @Override
    public void expireActiveToken(Long memberId) {
        Set<ActiveQueue> tokens = queueRepository.getActiveTokens();
        if (tokens.isEmpty()) {
            return;
        }

        for (ActiveQueue token : tokens) {
            if (token.compareTokenKey(memberId)) {
                queueRepository.removeActiveQueue(token.makeActiveKey());
                return;
            }
        }
    }
}
