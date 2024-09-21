package com.example.memberservice.interfaces;

import com.example.memberservice.domain.model.Member;
import com.example.memberservice.domain.repository.MemberRepository;
import com.example.memberservice.exception.ErrorEnum;
import com.example.memberservice.interfaces.api.common.response.CommonResponse;
import com.example.memberservice.interfaces.api.dto.MemberChargeRequest;
import com.example.memberservice.interfaces.api.dto.MemberResponse;
import com.example.memberservice.util.SlackClient;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@ActiveProfiles("container")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberControllerIntegrateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private SlackClient slackClient;

    @Container
    public static MySQLContainer<?> memberContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("member")
            .withUsername("myuser")
            .withPassword("myuser")
            .withInitScript("init-controller.sql");

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

    @DisplayName("현재 잔액을 조회한다")
    @Test
    void getBalance() {
        // given
        Long memberId = 1L;
        String url = "http://localhost:" + port + "/api/member/balance/" + memberId;

        List<Member> all = memberRepository.findAll();
        System.out.println("ALL :: " + all.get(0).getId());

        // when
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> header = new HttpEntity<>(headers);
        ResponseEntity<CommonResponse<MemberResponse>> response = restTemplate.exchange(url, HttpMethod.GET, header, new ParameterizedTypeReference<>() {});
        CommonResponse<MemberResponse> body = response.getBody();
        MemberResponse data = body.getData();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.SUCCESS.getMessage());
        assertThat(data.getBalance()).isEqualTo(5000);
    }

    @DisplayName("현재 잔액을 조회 시 멤버가 존재하지 않아 에러 발생한다")
    @Test
    void getBalance_no_member() {
        // given
        Long memberId = 2L;
        String url = "http://localhost:" + port + "/api/member/balance/" + memberId;

        // when
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> header = new HttpEntity<>(headers);
        ResponseEntity<CommonResponse<MemberResponse>> response = restTemplate.exchange(url, HttpMethod.GET, header, new ParameterizedTypeReference<>() {});
        CommonResponse<MemberResponse> body = response.getBody();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.MEMBER_NOT_FOUND.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.MEMBER_NOT_FOUND.getMessage());
    }

    @DisplayName("현재 잔액을 충전한다")
    @Test
    void chargeBalance() {
        // given
        Long memberId = 1L;
        String url = "http://localhost:" + port + "/api/member/balance";

        MemberChargeRequest request = new MemberChargeRequest(memberId, 5000L);

        // when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MemberChargeRequest> header = new HttpEntity<>(request,headers);

        ResponseEntity<CommonResponse<MemberResponse>> response = restTemplate.exchange(url, HttpMethod.POST, header, new ParameterizedTypeReference<>() {});
        CommonResponse<MemberResponse> body = response.getBody();
        MemberResponse data = body.getData();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.SUCCESS.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.SUCCESS.getMessage());
        assertThat(data.getBalance()).isEqualTo(10000L);
    }

    @DisplayName("현재 잔액을 조회 시 멤버가 존재하지 않아 에러 발생한다")
    @Test
    void chargeBalance_no_member() {
        // given
        Long memberId = 2L;
        String url = "http://localhost:" + port + "/api/member/balance";

        MemberChargeRequest request = new MemberChargeRequest(memberId, 5000L);

        // when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MemberChargeRequest> header = new HttpEntity<>(request,headers);
        ResponseEntity<CommonResponse<MemberResponse>> response = restTemplate.exchange(url, HttpMethod.POST, header, new ParameterizedTypeReference<>() {});
        CommonResponse<MemberResponse> body = response.getBody();

        // then
        assertThat(body.getCode()).isEqualTo(ErrorEnum.MEMBER_NOT_FOUND.getCode());
        assertThat(body.getMessage()).isEqualTo(ErrorEnum.MEMBER_NOT_FOUND.getMessage());
    }
}
