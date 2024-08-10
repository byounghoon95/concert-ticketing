package com.example.concertticketing.interfaces.api.queue;

import com.example.concertticketing.CommonControllerTest;
import com.example.concertticketing.interfaces.api.queue.dto.QueueRequest;
import com.example.concertticketing.domain.queue.model.Queue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

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
        Long position = 3L;

        QueueRequest request = new QueueRequest(memberId);

        Queue queue = new Queue(memberId, position);

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
                .andExpect(jsonPath("$.data.position").value(position))
        ;
    }

    @DisplayName("특정 멤버의 현재 대기열 정보를 반환한다")
    @Test
    void getInfo() throws Exception {
        // given
        Long memberId = 1L;
        Long position = 3L;

        Queue queue = new Queue(memberId, position);

        // when
        when(queueService.getInfo(any())).thenReturn(queue);

        // then
        mockMvc.perform(get("/api/queue/{memberId}", memberId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberId").value(memberId))
                .andExpect(jsonPath("$.data.position").value(position))
        ;
    }
}