package com.example.queueservice.interfaces;

import com.example.queueservice.domain.repository.QueueRepository;
import com.example.queueservice.exception.ErrorEnum;
import com.example.queueservice.interfaces.api.common.response.CommonResponse;
import com.example.queueservice.interfaces.api.dto.QueueRequest;
import com.example.queueservice.interfaces.api.dto.QueueResponse;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QueueControllerIntegrateTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Container
    public static MySQLContainer<?> memberContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("member")
            .withUsername("myuser")
            .withPassword("myuser")
            .withInitScript("init-controller.sql"); // Member 데이터 초기화

    @Container
    public static RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("redis:7"));

    @Container
    static GenericContainer<?> eurekaServer = new GenericContainer<>(DockerImageName.parse("leebyonghoon/eureka-server"))
            .withExposedPorts(9105);

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", memberContainer::getJdbcUrl);
        registry.add("spring.datasource.username", memberContainer::getUsername);
        registry.add("spring.datasource.password", memberContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", memberContainer::getDriverClassName);

        registry.add("spring.data.redis.host", () -> redisContainer.getHost());
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379).toString());
    }

    @BeforeAll
    static void beforeAll() {
        memberContainer.start();
        redisContainer.start();
        eurekaServer.start();
    }

    @AfterAll
    static void afterAll() {
        memberContainer.stop();
        eurekaServer.stop();
        redisContainer.stop();
    }

    private void setUpQueue() {


        Long memberId = 1L;
        Set<String> activeSet = Set.of(
                memberId + ":" + LocalDateTime.of(2024, 6, 12, 0, 0, 0).toEpochSecond(ZoneOffset.UTC),
                (memberId + 1) + ":" + LocalDateTime.of(2024, 6, 12, 0, 0, 0).toEpochSecond(ZoneOffset.UTC),
                (memberId + 2) + ":" + LocalDateTime.of(2024, 6, 12, 0, 5, 0).toEpochSecond(ZoneOffset.UTC),
                (memberId + 3) + ":" + LocalDateTime.of(2024, 6, 12, 0, 5, 0).toEpochSecond(ZoneOffset.UTC)
        );

        for (int i = 4; i < 6; i++) {
            LocalDateTime now = LocalDateTime.now();
            redisTemplate.opsForZSet().add("waitingTokens", String.valueOf(memberId + i), now.toEpochSecond(ZoneOffset.UTC));
        }

        redisTemplate.opsForSet().add("activeTokens", activeSet.toArray());
    }

    @AfterEach
    void afterEach() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
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
        assertThat(data.getPosition()).isEqualTo(1L);
    }

    @DisplayName("대기중인 사람이 있고 새로운 유저가 토큰을 발급한다")
    @Test
    void enqueue_wait_member_exist() {
        // given
        setUpQueue();
        Long memberId = 7L;
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
        assertThat(data.getPosition()).isEqualTo(3L);
    }

    @DisplayName("토큰이 만료되지 않은 유저가 토큰을 재발급하면 원래 존재하는 토큰을 반환한다")
    @Test
    void enqueue_already_get_token() {
        // given
        setUpQueue();
        Long memberId = 6L;
        Long queueId = 6L;
        String url = "http://localhost:" + port + "/api/queue/issue";

        QueueRequest request = new QueueRequest(memberId);

        // when
        HttpEntity<QueueRequest> header = setHeaderNoCheck(request);
        ResponseEntity<CommonResponse<QueueResponse>> response = restTemplate.exchange(url, HttpMethod.POST, header, new ParameterizedTypeReference<>() {});
        CommonResponse<QueueResponse> body = response.getBody();

        boolean inWait = queueRepository.isInWaitingTokens(queueId);

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.SUCCESS.getMessage());
        assertThat(inWait).isEqualTo(true);
    }

    @DisplayName("WAIT 상태인 현재 나의 토큰 정보를 반환한다")
    @Test
    void getInfo_wait() {
        // given
        setUpQueue();
        Long memberId = 6L;
        String url = "http://localhost:" + port + "/api/queue/" + memberId;

        QueueRequest request = new QueueRequest(memberId);

        // when
        HttpEntity<QueueRequest> header = setHeaderNoCheck(request);
        ResponseEntity<CommonResponse<QueueResponse>> response = restTemplate.exchange(url, HttpMethod.GET, header, new ParameterizedTypeReference<>() {});
        CommonResponse<QueueResponse> body = response.getBody();
        QueueResponse data = body.getData();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.SUCCESS.getMessage());
        assertThat(data.getMemberId()).isEqualTo(memberId);
        assertThat(data.getPosition()).isEqualTo(2);
    }

    @DisplayName("WAIT 상태의 토큰이 존재하지 않으면 에러를 반환한다")
    @Test
    void getInfo_token_expired() {
        // given
        setUpQueue();
        Long memberId = 1L;
        String url = "http://localhost:" + port + "/api/queue/" + memberId;

        // when
        ResponseEntity<CommonResponse<QueueResponse>> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null), new ParameterizedTypeReference<>() {});
        CommonResponse<QueueResponse> body = response.getBody();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.NO_WAIT_TOKEN.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.NO_WAIT_TOKEN.getMessage());
    }

    private HttpEntity<String> setHeader(Long memberId) {
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("memberId", String.valueOf(memberId));

        return new HttpEntity<>(headers);
    }

    private <T> HttpEntity<T> setHeader(Long memberId, T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("memberId", String.valueOf(memberId));
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(body, headers);
    }

    private <T> HttpEntity<T> setHeaderNoCheck(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(body, headers);
    }
}
