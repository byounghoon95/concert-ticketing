package com.example.concertticketing.api.queue;

import com.example.concertticketing.CommonControllerIntegrateTest;
import com.example.concertticketing.api.common.response.CommonResponse;
import com.example.concertticketing.api.queue.dto.QueueRequest;
import com.example.concertticketing.api.queue.dto.QueueResponse;
import com.example.concertticketing.domain.exception.ErrorEnum;
import com.example.concertticketing.domain.queue.model.Queue;
import com.example.concertticketing.domain.queue.model.QueueStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class QueueControllerIntegrateTest extends CommonControllerIntegrateTest {
    @AfterEach
    void tearDown() {
        queueRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("대기중인 사람이 없는 상태에서 토큰을 발급한다")
    @Test
    void enqueue_no_wait_member() {
        // given
        Long memberId = 1L;
        String url = "http://localhost:" + port + "/api/queue/issue";

        QueueRequest request = new QueueRequest(memberId);

        // when
        HttpEntity<QueueRequest> header = setHeaderNoCheck(request);
        ResponseEntity<CommonResponse<QueueResponse>> response = restTemplate.exchange(url, HttpMethod.POST, header, new ParameterizedTypeReference<>() {});
        CommonResponse<QueueResponse> body = response.getBody();
        QueueResponse data = body.getData();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.SUCCESS.getMessage());
        assertThat(data.getMemberId()).isEqualTo(memberId);
        assertThat(data.getStatus()).isEqualTo(QueueStatus.WAIT);
        assertThat(data.getPosition()).isEqualTo(0L);
    }

    @DisplayName("대기중인 사람이 있고 새로운 유저가 토큰을 발급한다")
    @Test
    void enqueue_wait_member_exist() {
        // given
        setUpQueue();
        Long memberId = findFirstMemberId() + 6L;
        String url = "http://localhost:" + port + "/api/queue/issue";

        QueueRequest request = new QueueRequest(memberId);

        // when
        HttpEntity<QueueRequest> header = setHeaderNoCheck(request);
        ResponseEntity<CommonResponse<QueueResponse>> response = restTemplate.exchange(url, HttpMethod.POST, header, new ParameterizedTypeReference<>() {});
        CommonResponse<QueueResponse> body = response.getBody();
        QueueResponse data = body.getData();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.SUCCESS.getMessage());
        assertThat(data.getMemberId()).isEqualTo(memberId);
        assertThat(data.getStatus()).isEqualTo(QueueStatus.WAIT);
        assertThat(data.getPosition()).isEqualTo(2L);
    }

    @DisplayName("토큰이 만료되지 않은 유저가 토큰을 재발급하면 원래 존재하는 토큰을 반환한다")
    @Test
    void enqueue_already_get_token() {
        // given
        setUpQueue();
        Long memberId = findFirstMemberId() + 5L;
        Long queueId = findFirstQueueId() + 5L;
        String url = "http://localhost:" + port + "/api/queue/issue";

        QueueRequest request = new QueueRequest(memberId);

        // when
        HttpEntity<QueueRequest> header = setHeaderNoCheck(request);
        ResponseEntity<CommonResponse<QueueResponse>> response = restTemplate.exchange(url, HttpMethod.POST, header, new ParameterizedTypeReference<>() {});
        CommonResponse<QueueResponse> body = response.getBody();
        QueueResponse data = body.getData();

        Queue queue = queueRepository.findById(queueId).get();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.SUCCESS.getMessage());
        assertThat(data.getToken()).isEqualTo(queue.getToken());
    }

    @DisplayName("active 상태인 현재 나의 토큰 정보를 반환한다")
    @Test
    void getInfo_active() {
        // given
        setUpQueue();
        Long memberId = findFirstMemberId() + 3L;
        Long queueId = findFirstQueueId() + 3L;
        String url = "http://localhost:" + port + "/api/queue/issue";

        QueueRequest request = new QueueRequest(memberId);

        // when
        HttpEntity<QueueRequest> header = setHeaderNoCheck(request);
        ResponseEntity<CommonResponse<QueueResponse>> response = restTemplate.exchange(url, HttpMethod.POST, header, new ParameterizedTypeReference<>() {});
        CommonResponse<QueueResponse> body = response.getBody();
        QueueResponse data = body.getData();

        Queue queue = queueRepository.findById(queueId).get();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.SUCCESS.getMessage());
        assertThat(data.getToken()).isEqualTo(queue.getToken());
    }

    @DisplayName("wait 상태인 현재 나의 토큰 정보를 반환한다")
    @Test
    void getInfo_wait() {
        // given
        setUpQueue();
        Long memberId = findFirstMemberId() + 5L;
        Long queueId = findFirstQueueId() + 5L;
        String url = "http://localhost:" + port + "/api/queue/issue";

        QueueRequest request = new QueueRequest(memberId);

        // when
        HttpEntity<QueueRequest> header = setHeaderNoCheck(request);
        ResponseEntity<CommonResponse<QueueResponse>> response = restTemplate.exchange(url, HttpMethod.POST, header, new ParameterizedTypeReference<>() {});
        CommonResponse<QueueResponse> body = response.getBody();
        QueueResponse data = body.getData();

        Queue queue = queueRepository.findById(queueId).get();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.SUCCESS.getMessage());
        assertThat(data.getToken()).isEqualTo(queue.getToken());
    }

    @DisplayName("토큰 정보 조회 시 토큰이 만료되어 에러를 반환한다")
    @Test
    void getInfo_token_expired() {
        // given
        setUpQueue();
        Long memberId = findFirstMemberId();
        String url = "http://localhost:" + port + "/api/queue/" + memberId;

        // when
        ResponseEntity<CommonResponse<QueueResponse>> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null), new ParameterizedTypeReference<>() {});
        CommonResponse<QueueResponse> body = response.getBody();
        QueueResponse data = body.getData();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.TOKEN_EXPIRED.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.TOKEN_EXPIRED.getMessage());
    }
}
