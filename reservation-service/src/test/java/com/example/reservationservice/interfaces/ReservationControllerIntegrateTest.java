package com.example.reservationservice.interfaces;

import com.example.reservationservice.domain.clients.QueueClient;
import com.example.reservationservice.domain.clients.SeatClient;
import com.example.reservationservice.domain.external.SeatResponse;
import com.example.reservationservice.domain.model.ReservationStatus;
import com.example.reservationservice.exception.ErrorEnum;
import com.example.reservationservice.interfaces.api.common.response.CommonResponse;
import com.example.reservationservice.interfaces.api.dto.ReserveSeatRequest;
import com.example.reservationservice.interfaces.api.dto.ReserveSeatResponse;
import com.example.reservationservice.util.SlackClient;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@Testcontainers
@ActiveProfiles("container")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReservationControllerIntegrateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @MockBean
    private SeatClient seatClient;

    @MockBean
    private SlackClient slackClient;

    @MockBean
    private QueueClient queueClient;

    @MockBean
    private ApplicationEventPublisher eventPublisher;

    @Container
    public static MySQLContainer<?> memberContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("member")
            .withUsername("myuser")
            .withPassword("myuser")
            .withInitScript("init-controller-member.sql");

    @Container
    public static MySQLContainer<?> concertContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("concert")
            .withUsername("myuser")
            .withPassword("myuser")
            .withInitScript("init-controller-concert.sql");

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
        concertContainer.start();
        redisContainer.start();
        eurekaServer.start();
    }

    @AfterAll
    static void afterAll() {
        memberContainer.stop();
        concertContainer.stop();
        eurekaServer.stop();
        redisContainer.stop();
    }

    void setUp() {
        Long memberId = 1L;
        Set<String> activeSet = Set.of(
                memberId + ":" + LocalDateTime.now().plusMinutes(1).toEpochSecond(ZoneOffset.UTC)
        );

        redisTemplate.opsForSet().add("activeTokens", activeSet.toArray());
    }

    @DisplayName("좌석을 예약한다")
    @Test
    void reserveSeat() {
        setUp();
        Long memberId = 1L;
        Long seatId = 1L;
        String url = "http://localhost:" + port + "/api/reserve";

        ReserveSeatRequest request = new ReserveSeatRequest(seatId,memberId);

        // when
        when(queueClient.verifyToken(memberId)).thenReturn(true);
        HttpEntity<ReserveSeatRequest> header = setHeader(memberId, request);
        ResponseEntity<CommonResponse<ReserveSeatResponse>> response = restTemplate.exchange(url, HttpMethod.POST, header, new ParameterizedTypeReference<>() {});
        CommonResponse<ReserveSeatResponse> body = response.getBody();
        ReserveSeatResponse data = body.getData();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.SUCCESS.getMessage());
        assertThat(data.getStatus()).isEqualTo(ReservationStatus.RESERVED);
    }

    @DisplayName("임시저장된 좌석 예약 시 에러를 반환한다")
    @Test
    void reserveSeat_already_reserved() {
        setUp();
        Long memberId = 1L;
        Long seatId = 2L;
        String url = "http://localhost:" + port + "/api/reserve";

        ReserveSeatRequest request = new ReserveSeatRequest(seatId,memberId);

        // when
        when(queueClient.verifyToken(memberId)).thenReturn(true);
        HttpEntity<ReserveSeatRequest> header = setHeader(memberId, request);
        ResponseEntity<CommonResponse<ReserveSeatResponse>> response = restTemplate.exchange(url, HttpMethod.POST, header, new ParameterizedTypeReference<>() {});
        CommonResponse<ReserveSeatResponse> body = response.getBody();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.RESERVED_SEAT.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.RESERVED_SEAT.getMessage());
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
