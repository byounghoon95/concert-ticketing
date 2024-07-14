package com.example.concertticketing.api.queue;

import com.example.concertticketing.CommonControllerTest;
import com.example.concertticketing.api.queue.dto.QueueRequest;
import com.example.concertticketing.domain.member.model.Member;
import com.example.concertticketing.domain.queue.model.Queue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QueueControllerTest extends CommonControllerTest {

    @DisplayName("대기열 진입 후 토큰을 발급한다")
    @Test
    void enqueue() throws Exception {
        // given
        Long memberId = 1L;
        UUID uuid = UUID.randomUUID();
        Long position = 3L;

        Member member = Member.builder()
                .id(memberId)
                .build();
        QueueRequest request = new QueueRequest(memberId);

        Queue queue = Queue.builder()
                .token(uuid)
                .member(member)
                .position(position)
                .build();

        // when
        when(queueService.enqueue(any())).thenReturn(queue);

        // then
        mockMvc.perform(post("/api/queue/issue")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberId").value(memberId))
                .andExpect(jsonPath("$.data.token").value(uuid.toString()))
                .andExpect(jsonPath("$.data.position").value(position))
        ;
    }

    @DisplayName("특정 멤버의 현재 대기열 정보를 반환한다")
    @Test
    void getInfo() throws Exception {
        // given
        Long memberId = 1L;
        UUID uuid = UUID.randomUUID();
        Long position = 3L;
        Member member = Member.builder()
                .id(memberId)
                .build();
        Queue queue = Queue.builder()
                .token(uuid)
                .member(member)
                .position(position)
                .build();

        // when
        when(queueService.getInfo(any())).thenReturn(queue);

        // then
        mockMvc.perform(get("/api/queue/{memberId}", memberId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberId").value(memberId))
                .andExpect(jsonPath("$.data.token").value(uuid.toString()))
                .andExpect(jsonPath("$.data.position").value(position))
        ;
    }
}