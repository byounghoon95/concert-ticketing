package com.example.memberservice.domain;

import com.example.memberservice.domain.model.Member;
import com.example.memberservice.domain.repository.MemberRepository;
import com.example.memberservice.domain.service.MemberService;
import com.example.memberservice.interfaces.api.dto.MemberChargeRequest;
import com.example.memberservice.util.SlackClient;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@ActiveProfiles("container")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberServiceIntegrateTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private SlackClient slackClient;

    @Container
    public static MySQLContainer<?> memberContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("member")
            .withUsername("myuser")
            .withPassword("myuser")
            .withInitScript("init-service.sql");

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

    @DisplayName("동시에 3개의 충전 요청이 들어오고 하나만 성공한다")
    @Test
    void chargeBalance() {
        // given
        Member member = memberRepository.findById(1L).get();
        Long memberId = 1L;
        Long chargeAmount = 5000L;

        MemberChargeRequest request = new MemberChargeRequest(memberId, chargeAmount);

        // when
        List<CompletableFuture<Void>> futures =
                List.of(
                        CompletableFuture.runAsync(() -> memberService.chargeBalance(request)),
                        CompletableFuture.runAsync(() -> memberService.chargeBalance(request)),
                        CompletableFuture.runAsync(() -> memberService.chargeBalance(request))
                );

        futures.stream()
                .forEach(future -> {
                    try {
                        future.join();
                    } catch (Exception e) {
                        System.out.println("Error : " + e.getMessage());
                    }
                });

        Member updatedMember = memberRepository.findById(memberId).get();

        // then
        assertThat(updatedMember.getBalance()).isEqualTo(member.getBalance() + chargeAmount);
    }
}