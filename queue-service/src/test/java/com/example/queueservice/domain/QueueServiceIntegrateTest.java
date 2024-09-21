package com.example.queueservice.domain;

import com.example.queueservice.domain.repository.QueueRepository;
import com.example.queueservice.domain.service.QueueService;
import com.redis.testcontainers.RedisContainer;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * findFirstMemberId, findFirstQueueId
 * + 0 ~ 2L : 만료 토근 조회
 * + 2L ~ 5L : 활성 토큰 조회
 * + 6L ~ 8L : 대기 토큰 조회
 * */
@Testcontainers
@ActiveProfiles("container")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QueueServiceIntegrateTest {
    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private QueueService queueService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Container
    public static MySQLContainer<?> memberContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("member")
            .withUsername("myuser")
            .withPassword("myuser")
            .withInitScript("init-service.sql"); // Member 데이터 초기화

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

    @AfterEach
    void afterEach() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Autowired
    private EntityManager entityManager;

    void setUp() {
        Long memberId = 1L;
        for (int i = 6; i < 9; i++) {
            LocalDateTime now = LocalDateTime.now();
            redisTemplate.opsForZSet().add("waitingTokens", String.valueOf(memberId + i), now.toEpochSecond(ZoneOffset.UTC));
        }

        Set<String> activeSet = Set.of(
                memberId + 3L + ":" + LocalDateTime.of(2024, 6, 12, 0, 5, 0).toEpochSecond(ZoneOffset.UTC),
                memberId + 4L + ":" + LocalDateTime.of(2024, 6, 12, 0, 5, 0).toEpochSecond(ZoneOffset.UTC),
                memberId + 5L + ":" + LocalDateTime.of(2024, 6, 12, 0, 5, 0).toEpochSecond(ZoneOffset.UTC)
        );

        redisTemplate.opsForSet().add("activeTokens", activeSet.toArray());
    }

    @DisplayName("토큰이 유효한지 검증하고 유효하면 true 를 반환한다")
    @Test
    void verify_true() {
        // given
        setUp();
        Long memberId = 5L;

        // when
        boolean verify = queueService.verify(memberId);

        // then
        assertThat(verify).isEqualTo(true);
    }

    @DisplayName("토큰이 유효한지 검증하고 유효하지 않으면 false 를 반환한다")
    @Test
    void verify_false() {
        // given
        setUp();
        Long memberId = 1L;

        // when
        boolean verify = queueService.verify(memberId);

        // then
        assertThat(verify).isEqualTo(false);
    }

    @DisplayName("활성 토큰 중 만료 기간이 지난 토큰을 만료시킨다")
    @Test
    void updateActiveTokenToExpired() {
        // given
        setUp();

        String key = 6L + ":" + LocalDateTime.of(2024, 6, 12, 0, 5, 0).toEpochSecond(ZoneOffset.UTC);
        LocalDateTime now = LocalDateTime.of(2024, 6, 12, 0, 6, 0);

        // when
        boolean prev = queueRepository.isInActiveTokens(key);
        queueService.expireActiveTokens(now);
        boolean curr = queueRepository.isInActiveTokens(key);


        // then
        assertThat(prev).isEqualTo(true);
        assertThat(curr).isEqualTo(false);
    }

    @DisplayName("활성 토큰 중 만료 기간이 지나지 않았으면 작업을 하지 않는다")
    @Test
    void updateActiveTokenToExpired_fail() {
        // given
        setUp();

        String key = 6L + ":" + LocalDateTime.of(2024, 6, 12, 0, 5, 0).toEpochSecond(ZoneOffset.UTC);
        LocalDateTime now = LocalDateTime.of(2024, 6, 12, 0, 4, 0);

        // when
        boolean prev = queueRepository.isInActiveTokens(key);
        queueService.expireActiveTokens(now);
        boolean curr = queueRepository.isInActiveTokens(key);

        // then
        assertThat(prev).isEqualTo(true);
        assertThat(curr).isEqualTo(true);
    }

    @DisplayName("대기중인 토큰을 활성상태로 변경한다")
    @Test
    void updateWaitTokenToActive() {
        // given
        setUp();
        Long memberId = 9L;
        LocalDateTime now = LocalDateTime.of(2024, 6, 12, 0, 5, 0);

        // when
        boolean prev = queueRepository.isInWaitingTokens(memberId);
        // 입장 가능한 인원만큼 변경 가능한데 현재 ACTIVE 인원이 3이고
        // queueId가 WAIT 의 마지막이라 3명이 더 입장가능한 available 값을 설정
        queueService.updateWaitTokenToActive(now,6);
        boolean curr = queueRepository.isInWaitingTokens(memberId);

        // then
        assertThat(prev).isEqualTo(true);
        assertThat(curr).isEqualTo(false);
    }
}
