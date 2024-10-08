package com.example.queueservice.domain.repository;


import com.example.queueservice.domain.model.ActiveQueue;

import java.util.Set;

public interface QueueRepository {

    void addWaitingQueue(Long memberId);

    boolean isInWaitingTokens(Long memberId);

    Long getPosition(Long memberId);

    Set<String> getWaitTokens(int count);

    void removeWaitQueue(String memberId);

    void removeWaitQueues(Set<String> members);

    void addActiveQueues(Set<String> members);

    Set<ActiveQueue> getActiveTokens();

    boolean isInActiveTokens(String value);

    void removeActiveQueue(String value);

    void flushAll();
}
