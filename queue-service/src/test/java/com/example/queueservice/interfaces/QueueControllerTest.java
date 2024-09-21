package com.example.queueservice.interfaces;

import com.example.queueservice.domain.model.Queue;
import com.example.queueservice.domain.service.QueueServiceImpl;
import com.example.queueservice.interfaces.api.dto.QueueRequest;
import com.example.queueservice.util.SlackClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest
class QueueControllerTest {

    @MockBean
    private QueueServiceImpl queueService;

    @MockBean
    private SlackClient slackClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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