package com.example.concertservice.interfaces;

import com.example.concertservice.domain.model.ConcertDate;
import com.example.concertservice.domain.model.ConcertDateDetails;
import com.example.concertservice.domain.model.ConcertSeat;
import com.example.concertservice.domain.model.ConcertSeatDetail;
import com.example.concertservice.exception.ErrorEnum;
import com.example.concertservice.interfaces.api.common.response.CommonResponse;
import com.example.concertservice.util.SlackClient;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
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

@ActiveProfiles("container")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConcertControllerIntegrateTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    @MockBean
    private SlackClient slackClient;

    @Autowired
    private RedisTemplate redisTemplate;

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

        registry.add("spring.datasource.url", concertContainer::getJdbcUrl);
        registry.add("spring.datasource.username", concertContainer::getUsername);
        registry.add("spring.datasource.password", concertContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", concertContainer::getDriverClassName);

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

    @DisplayName("예약 가능한 날짜 목록을 조회한다")
    @Test
    void getAvailableDates() {
        // given
        setUp();
        Long concertId = 1L;
        Long concertDetailId = 1L;
        Long memberId = 1L;
        String url = "http://localhost:" + port + "/api/concert/date/" + concertId;

        // HttpEntity 에 헤더 포함
        HttpEntity<String> header = setHeader(memberId);

        // when
        ResponseEntity<CommonResponse<ConcertDate>> response = restTemplate.exchange(url, HttpMethod.GET, header, new ParameterizedTypeReference<>() {});
        CommonResponse<ConcertDate> body = response.getBody();
        ConcertDate concertDate = body.getData();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.SUCCESS.getMessage());
        assertThat(concertDate.concertId()).isEqualTo(concertId);
        assertThat(concertDate.concertDates().size()).isEqualTo(3);
        assertThat(concertDate.concertDates().get(0)).isInstanceOf(ConcertDateDetails.class);
        assertThat(concertDate.concertDates().get(1).concertDetailId()).isEqualTo(concertDetailId + 1);
        assertThat(concertDate.concertDates().get(1).dates()).isEqualTo(LocalDateTime.of(2024, 6, 12, 0, 0, 0).plusDays(1));
    }

    @DisplayName("예약 가능한 날짜 목록 조회 시 토큰이 없어 실패한다")
    @Test
    void getAvailableDates_fail_no_token() {
        // given
        setUp();
        Long concertId = 1L;
        Long memberId = 2L;
        String url = "http://localhost:" + port + "/api/concert/date/" + concertId;

        // HttpEntity 에 헤더 포함
        HttpEntity<String> header = setHeader(memberId);

        // when
        ResponseEntity<CommonResponse<ConcertDate>> response = restTemplate.exchange(url, HttpMethod.GET, header, new ParameterizedTypeReference<>() {});
        CommonResponse<ConcertDate> body = response.getBody();
        ConcertDate concertDate = body.getData();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.TOKEN_EXPIRED.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.TOKEN_EXPIRED.getMessage());
        assertThat(concertDate).isNull();
    }

    @DisplayName("예약 가능한 좌석 목록을 조회한다")
    @Test
    void getAvailableSeats() {
        // given
        setUp();
        Long concertDetailId = 1L;
        Long memberId = 1L;
        String url = "http://localhost:" + port + "/api/concert/seat/" + concertDetailId;

        // HttpEntity 에 헤더 포함
        HttpEntity<String> header = setHeader(memberId);

        // when
        ResponseEntity<CommonResponse<ConcertSeat>> response = restTemplate.exchange(url, HttpMethod.GET, header, new ParameterizedTypeReference<>() {});
        CommonResponse<ConcertSeat> body = response.getBody();
        ConcertSeat concertDate = body.getData();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.SUCCESS.getMessage());
        assertThat(concertDate.concertDetailId()).isEqualTo(concertDetailId);
        assertThat(concertDate.concertSeats().size()).isEqualTo(5);
        assertThat(concertDate.concertSeats().get(0)).isInstanceOf(ConcertSeatDetail.class);
        assertThat(concertDate.concertSeats().get(0).seatNo()).isEqualTo(1);
    }

    @DisplayName("예약 가능한 좌석 목록을 조회 시 토큰이 없어 실패한다")
    @Test
    void getAvailableSeats_fail_no_token() {
        // given
        setUp();
        Long concertDetailId = 1L;
        Long memberId = 2L;
        String url = "http://localhost:" + port + "/api/concert/seat/" + concertDetailId;

        // HttpEntity 에 헤더 포함
        HttpEntity<String> header = setHeader(memberId);

        // when
        ResponseEntity<CommonResponse<ConcertSeat>> response = restTemplate.exchange(url, HttpMethod.GET, header, new ParameterizedTypeReference<>() {});
        CommonResponse<ConcertSeat> body = response.getBody();
        ConcertSeat concertDate = body.getData();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.TOKEN_EXPIRED.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.TOKEN_EXPIRED.getMessage());
        assertThat(concertDate).isNull();
    }

    protected HttpEntity<String> setHeader(Long memberId) {
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("memberId", String.valueOf(memberId));

        return new HttpEntity<>(headers);
    }

    protected <T> HttpEntity<T> setHeader(Long memberId, T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("memberId", String.valueOf(memberId));
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(body, headers);
    }

    protected <T> HttpEntity<T> setHeaderNoCheck(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(body, headers);
    }
}
